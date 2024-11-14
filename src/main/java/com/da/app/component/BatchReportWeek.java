package com.da.app.component;

import com.da.app.domain.Course;
import com.da.app.domain.User;
import com.da.app.domain.WeeklyReport;
import com.da.app.dto.ReportCourseDto;
import com.da.app.repository.CourseRepository;
import com.da.app.repository.EnrollmentRepository;
import com.da.app.repository.UserRepository;
import com.da.app.repository.WeeklyReportRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@EnableScheduling
public class BatchReportWeek {
    private static final Logger log = LoggerFactory.getLogger(BatchReportWeek.class);

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final CourseRepository courseRepository;
    private final WeeklyReportRepository weeklyReportRepository;
    private final JobLauncher jobLauncher;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final JavaMailSender javaMailSender;

    @Bean
    @JobScope
    public ItemReader<ReportCourseDto> reader() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(7);
        List<ReportCourseDto> courseData = courseRepository.findCoursesWithEnrollmentCount(sevenDaysAgo, now);
        return new ListItemReader<>(courseData);
    }

    @Bean
    @JobScope
    public CourseReportProcessor processor() {
        return new CourseReportProcessor();
    }

    @Bean
    @JobScope
    public FlatFileItemWriter<ReportCourseDto> writer() {
        String directoryPath = "C:\\Users\\luanlv\\IdeaProjects\\Case_Study_3\\store";
        createDirectoryIfNotExists(directoryPath);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(7);
        String startDateStr = sevenDaysAgo.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String endDateStr = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        String filePath = directoryPath + File.separator + "course_report_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";

        log.info("Saving CSV to file: {}", filePath);

        FlatFileItemWriter<ReportCourseDto> csvWriter = new FlatFileItemWriter<>();
        csvWriter.setResource(new FileSystemResource(filePath));

        DelimitedLineAggregator<ReportCourseDto> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");

        BeanWrapperFieldExtractor<ReportCourseDto> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"name", "registered"});
        lineAggregator.setFieldExtractor(fieldExtractor);

        csvWriter.setLineAggregator(lineAggregator);
        csvWriter.setHeaderCallback(writer -> writer.write("name,registered"));

        List<ReportCourseDto> courses = courseRepository.findCoursesWithEnrollmentCount(LocalDateTime.now().minusDays(7), LocalDateTime.now());

        WeeklyReport weeklyReport = new WeeklyReport();
        weeklyReport.setName("Báo cáo ngày " + startDateStr + "_" + endDateStr);
        weeklyReport.setType("Báo cáo tuần");
        weeklyReport.setCreator("Hệ thống");
        weeklyReport.setWeekStartDate(sevenDaysAgo.toLocalDate());
        weeklyReport.setWeekEndDate(now.toLocalDate());
        weeklyReport.setReportPath(filePath);
        weeklyReport.setCreatedAt(LocalDateTime.now());
        weeklyReportRepository.save(weeklyReport);

        if (courses.isEmpty()) {
            Chunk<ReportCourseDto> emptyChunk = new Chunk<>(Collections.emptyList());
            try {
                csvWriter.open(new ExecutionContext());
                csvWriter.write(emptyChunk);
                log.info("No data found. Empty CSV file created at {}", filePath);
                csvWriter.close();
            } catch (Exception e) {
                log.error("Error creating empty CSV file: {}", e.getMessage());
            }
        } else {
            Chunk<ReportCourseDto> chunkWithData = new Chunk<>(courses);
            try {
                csvWriter.open(new ExecutionContext());
                csvWriter.write(chunkWithData);
                log.info("Data found. CSV file created with data at {}", filePath);
                csvWriter.close();
            } catch (Exception e) {
                log.error("Error writing CSV file: {}", e.getMessage());
            }
        }

        return csvWriter;
    }

    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .<ReportCourseDto, ReportCourseDto>chunk(10, platformTransactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Job reportJob() {
        log.info("Starting report job...");
        return new JobBuilder("reportJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step1())
                .listener(jobExecutionListener())
                .build();
    }

    @Bean
    public JobExecutionListenerSupport jobExecutionListener() {
        return new JobExecutionListenerSupport() {
            @Override
            public void afterJob(org.springframework.batch.core.JobExecution jobExecution) {
                if (jobExecution.getStatus() == org.springframework.batch.core.BatchStatus.COMPLETED) {
                    log.info("Job completed successfully.");
                }
            }
        };
    }

    private void createDirectoryIfNotExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                log.info("Directory created: {}", directoryPath);
            } else {
                log.error("Failed to create directory: {}", directoryPath);
            }
        } else {
            log.info("Directory already exists: {}", directoryPath);
        }
    }

    @Scheduled(cron = "0 0 6 ? * MON")
    public void perform() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("runTime", System.currentTimeMillis())
                .toJobParameters();
        JobExecution execution = jobLauncher.run(reportJob(), jobParameters);
        log.info("Job Execution Status: {}", execution.getStatus());
    }
}

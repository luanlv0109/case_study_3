package com.da.app.component;

import com.da.app.domain.Course;
import com.da.app.domain.User;
import com.da.app.repository.CourseRepository;
import com.da.app.repository.EnrollmentRepository;
import com.da.app.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableBatchProcessing
@EnableScheduling
@RequiredArgsConstructor
public class BatchNotificationMail {

    private static final Logger log = LoggerFactory.getLogger(BatchNotificationMail.class);

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JobLauncher jobLauncher;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final JavaMailSender javaMailSender;

    @Bean
    public Job sendCourseNotificationsJob() {
        return new JobBuilder("sendCourseNotificationsJob", jobRepository)
                .start(sendCourseNotificationsStep())
                .build();
    }

    @Bean
    public Step sendCourseNotificationsStep() {
        return new StepBuilder("sendCourseNotificationsStep", jobRepository)
                .<User, User>chunk(1, transactionManager)
                .reader(studentReader())
                .processor(emailNotificationProcessor())
                .writer(emailNotificationWriter())
                .build();
    }

    @Bean
    @JobScope

    public ItemReader<User> studentReader() {
        List<User> students = userRepository.findByRole("student");
        log.info("Total students read: {}", students.size());
        return new ListItemReader<>(students);
    }

    @Bean
    @JobScope

    public ItemProcessor<User, User> emailNotificationProcessor() {
        return student -> {
            log.info("Processing student: {}", student.getUsername());

            List<Course> coursesNotStarted = courseRepository.findByStartDateAfter(LocalDate.now());
            log.info("Total upcoming courses: {}", coursesNotStarted.size());

            List<Course> availableCourses = coursesNotStarted.stream()
                    .filter(course -> !enrollmentRepository.existsByStudentAndCourse(student, course))
                    .collect(Collectors.toList());

            if (!availableCourses.isEmpty()) {
                log.info("Available courses for student {}: {}", student.getUsername(), availableCourses.size());
                sendEmail(student, availableCourses);
            } else {
                log.info("No available courses for student: {}", student.getUsername());
            }

            return student;
        };
    }

    @Bean
    @JobScope

    public ItemWriter<User> emailNotificationWriter() {
        return students -> log.info("Email sending completed for students.");
    }

    private void sendEmail(User student, List<Course> availableCourses) {
        StringBuilder emailBody = new StringBuilder("Chào " + student.getUsername() + ",\n\nBạn chưa đăng ký các khóa học sau:\n\n");
        for (Course course : availableCourses) {
            emailBody.append("Tên khóa học: ").append(course.getCourseName())
                    .append("\nNgày bắt đầu: ").append(course.getStartDate())
                    .append("\nNgày kết thúc: ").append(course.getEndDate()).append("\n\n");
        }

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(student.getUsername());
            helper.setSubject("Thông báo về khóa học chưa bắt đầu");
            helper.setText(emailBody.toString());
            javaMailSender.send(message);
            log.info("Email sent to: {}", student.getUsername());
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", student.getUsername(), e);
        }
    }


    @Scheduled(cron = "0 0 6 ? * MON")
    public void perform() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("runTime", System.currentTimeMillis())
                .toJobParameters();
        JobExecution execution = jobLauncher.run(sendCourseNotificationsJob(), jobParameters);
        log.info("Job Execution Status: {}", execution.getStatus());
    }

}

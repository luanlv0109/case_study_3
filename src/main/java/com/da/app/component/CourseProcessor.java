package com.da.app.component;
import com.da.app.domain.Course;
import com.da.app.domain.User;
import com.da.app.repository.CourseRepository;
import com.da.app.repository.EnrollmentRepository;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class CourseProcessor implements ItemProcessor<User, List<Course>> {
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public CourseProcessor(CourseRepository courseRepository, EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public List<Course> process(User student) throws Exception {
        // Lọc các khóa học chưa bắt đầu và sinh viên chưa đăng ký
        List<Course> coursesNotStarted = courseRepository.findByStartDateAfter(LocalDate.now());
        return coursesNotStarted.stream()
                .filter(course -> !enrollmentRepository.existsByStudentAndCourse(student, course))
                .collect(Collectors.toList());
    }
}


package com.da.app.service.course;

import com.da.app.domain.Course;
import com.da.app.domain.Enrollment;
import com.da.app.dto.*;
import com.da.app.repository.CourseRepository;
import com.da.app.repository.EnrollmentRepository;
import com.da.app.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CCourseServiceImpl implements ICourseService {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<CourseManageDto> filterCourse(int pageSize, int pageIndex) {
        return courseRepository.pageFilterCourse(PageRequest.of(pageIndex, pageSize));
    }

    @Override
    public Page<CourseDTO> filterCourseForStudent(int pageSize, int pageIndex, Long studentId) {
        return courseRepository.pageFilterCourseForStudent(studentId, PageRequest.of(pageIndex, pageSize));
    }

    @Override
    public Page<CourseDTO> filterCourseByStudentId(int pageSize, int pageIndex, Long studentId) {
        return courseRepository.PageFilterCoursesByStudentId(studentId, PageRequest.of(pageIndex, pageSize));
    }

    @Override
    public DetailSourceDto getDetailSourceByCourseID(Long id) {
        DetailSourceDto course = courseRepository.getDetailCourseByCourseId(id);
        List<EnrollmentStudentDto> enrollmentStudentDtoList = enrollmentRepository.getEnrollmentStudentByCourseId(id);
        course.setEnrollmentStudents(enrollmentStudentDtoList);
        return course;
    }

    @Override
    @Transactional
    public boolean registerStudentForCourse(Long studentId, Long courseId) {
        if (enrollmentRepository.findByCourseIdAndStudentId(courseId, studentId).isPresent() ||
                userRepository.findById(studentId).isEmpty() ||
                courseRepository.findById(courseId).isEmpty()) {
            return false;
        }

        Course courseEntity = courseRepository.findById(courseId).get();
        if (courseEntity.getEnrolledStudents() >= courseEntity.getMaxStudents()) {
            return false;
        }

        Enrollment newEnrollment = new Enrollment();
        newEnrollment.setCourse(courseEntity);
        newEnrollment.setStudent(userRepository.findById(studentId).get());
        enrollmentRepository.save(newEnrollment);

        if (courseEntity.getEnrolledStudents() < courseEntity.getMaxStudents()) {
            courseEntity.setEnrolledStudents(courseEntity.getEnrolledStudents() + 1);
            courseRepository.save(courseEntity);
        }

        return true;
    }

    @Override
    @Transactional
    public boolean unregisterStudentFromCourse(Long studentId, Long courseId) {
        Optional<Enrollment> enrollment = enrollmentRepository.findByCourseIdAndStudentId(courseId, studentId);
        if (enrollment.isEmpty() || userRepository.findById(studentId).isEmpty() ||
                courseRepository.findById(courseId).isEmpty() ||
                LocalDate.now().isAfter(courseRepository.findById(courseId).get().getStartDate())) {
            return false;
        }

        enrollmentRepository.delete(enrollment.get());

        Course courseEntity = courseRepository.findById(courseId).get();
        if (courseEntity.getEnrolledStudents() > 0) {
            courseEntity.setEnrolledStudents(courseEntity.getEnrolledStudents() - 1);
            courseRepository.save(courseEntity);
        }

        return true;
    }

    @Override
    @Transactional
    public boolean deleteCourse(Long id) {
        Optional<Course> course = courseRepository.findById(id);
        if (course.isEmpty()) {
            return false;
        }

        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(id);
        if (!enrollments.isEmpty()) {
            enrollmentRepository.deleteAll(enrollments);
        }

        courseRepository.delete(course.get());
        return true;
    }

    @Override
    @Transactional
    public boolean updateCourse(Long courseId, CourseInput courseInput) {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            log.error("Course with ID " + courseId + " not found.");
            return false;
        }

        Course course = courseOptional.get();
        course.setCourseName(courseInput.getName());
        course.setDescription(courseInput.getDescription());
        course.setMaxStudents(courseInput.getMaxStudents());
        course.setStartDate(courseInput.getStartDate());
        course.setEndDate(courseInput.getEndDate());

        courseRepository.save(course);
        log.info("Course with ID " + courseId + " has been updated.");
        return true;
    }

    @Transactional
    @Override
    public boolean createCourse(CourseInput courseInput) {
        // Kiểm tra xem khóa học có hợp lệ hay không
        if (courseInput.getName() == null || courseInput.getMaxStudents() <= 0 || courseInput.getStartDate() == null || courseInput.getEndDate() == null) {
            return false;
        }

        // Tạo đối tượng khóa học mới từ CourseInput
        Course newCourse = new Course();
        newCourse.setCourseName(courseInput.getName());
        newCourse.setDescription(courseInput.getDescription());
        newCourse.setMaxStudents(courseInput.getMaxStudents());
        newCourse.setStartDate(courseInput.getStartDate());
        newCourse.setEndDate(courseInput.getEndDate());
        newCourse.setEnrolledStudents(0); // Khởi tạo số lượng sinh viên đã đăng ký là 0
        newCourse.setCreatedAt(LocalDateTime.now());
        // Lưu khóa học vào cơ sở dữ liệu
        courseRepository.save(newCourse);
        log.info("Course with name " + courseInput.getName() + " has been created.");

        return true;
    }
}

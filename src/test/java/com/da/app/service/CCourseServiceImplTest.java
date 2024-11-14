package com.da.app.service;

import com.da.app.domain.Course;
import com.da.app.domain.Enrollment;
import com.da.app.domain.User;
import com.da.app.dto.*;
import com.da.app.repository.CourseRepository;
import com.da.app.repository.EnrollmentRepository;
import com.da.app.repository.UserRepository;
import com.da.app.service.course.CCourseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CCourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CCourseServiceImpl courseService;

    private Course course;
    private Enrollment enrollment;

    @BeforeEach
    void setUp() {
        course = new Course();
        course.setId(1L);
        course.setCourseName("Test Course");
        course.setMaxStudents(10);
        course.setEnrolledStudents(0);
        course.setStartDate(LocalDate.now().plusDays(1));
        course.setEndDate(LocalDate.now().plusMonths(1));

        enrollment = new Enrollment();
        enrollment.setCourse(course);
    }

    @Test
    void testFilterCourse() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<CourseManageDto> coursePage = new PageImpl<>(Collections.emptyList());
        when(courseRepository.pageFilterCourse(pageRequest)).thenReturn(coursePage);

        Page<CourseManageDto> result = courseService.filterCourse(10, 0);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(courseRepository).pageFilterCourse(pageRequest);
    }

    @Test
    void testRegisterStudentForCourse_Success() {
        Long studentId = 1L;
        Long courseId = 1L;

        when(enrollmentRepository.findByCourseIdAndStudentId(courseId, studentId)).thenReturn(Optional.empty());
        User mockUser = new User();  // Replace with actual User class
        when(userRepository.findById(studentId)).thenReturn(Optional.of(mockUser));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        boolean result = courseService.registerStudentForCourse(studentId, courseId);

        assertTrue(result);
        verify(enrollmentRepository).save(any(Enrollment.class));
        verify(courseRepository).save(course);
    }

    @Test
    void testRegisterStudentForCourse_AlreadyRegistered() {
        Long studentId = 1L;
        Long courseId = 1L;

        when(enrollmentRepository.findByCourseIdAndStudentId(courseId, studentId)).thenReturn(Optional.of(enrollment));

        boolean result = courseService.registerStudentForCourse(studentId, courseId);

        assertFalse(result);
        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void testUnregisterStudentFromCourse_Success() {
        Long studentId = 1L;
        Long courseId = 1L;
        course.setStartDate(LocalDate.now().plusDays(1));
        course.setEnrolledStudents(1);  // Ensure there are enrolled students to satisfy the condition

        when(enrollmentRepository.findByCourseIdAndStudentId(courseId, studentId)).thenReturn(Optional.of(enrollment));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        User mockUser = new User();  // Replace with actual User class
        when(userRepository.findById(studentId)).thenReturn(Optional.of(mockUser));

        boolean result = courseService.unregisterStudentFromCourse(studentId, courseId);

        assertTrue(result);
        verify(enrollmentRepository).delete(enrollment);
        verify(courseRepository).save(course);  // This should now be invoked
    }

    @Test
    void testUnregisterStudentFromCourse_AfterStartDate() {
        Long studentId = 1L;
        Long courseId = 1L;
        course.setStartDate(LocalDate.now().minusDays(1));  // Set start date in the past


        boolean result = courseService.unregisterStudentFromCourse(studentId, courseId);

        assertFalse(result);
        verify(enrollmentRepository, never()).delete(any());
    }

    @Test
    void testDeleteCourse_Success() {
        Long courseId = 1L;

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.findByCourseId(courseId)).thenReturn(Collections.emptyList());

        boolean result = courseService.deleteCourse(courseId);

        assertTrue(result);
        verify(courseRepository).delete(course);
    }

    @Test
    void testUpdateCourse_Success() {
        Long courseId = 1L;
        CourseInput courseInput = new CourseInput();
        courseInput.setName("Updated Course");
        courseInput.setMaxStudents(20);
        courseInput.setStartDate(LocalDate.now().plusDays(1));
        courseInput.setEndDate(LocalDate.now().plusMonths(2));

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        boolean result = courseService.updateCourse(courseId, courseInput);

        assertTrue(result);
        assertEquals("Updated Course", course.getCourseName());
        verify(courseRepository).save(course);
    }

    @Test
    void testCreateCourse_Success() {
        CourseInput courseInput = new CourseInput();
        courseInput.setName("New Course");
        courseInput.setMaxStudents(10);
        courseInput.setStartDate(LocalDate.now().plusDays(1));
        courseInput.setEndDate(LocalDate.now().plusMonths(1));

        boolean result = courseService.createCourse(courseInput);

        assertTrue(result);
        verify(courseRepository).save(any(Course.class));
    }
}

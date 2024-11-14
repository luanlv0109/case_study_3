package com.da.app.repository;

import com.da.app.domain.Course;
import com.da.app.domain.Enrollment;
import com.da.app.domain.User;
import com.da.app.dto.EnrollmentStudentDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByCourseIdAndStudentId(Long courseId, Long studentId);
    @Query(
            "SELECT new com.da.app.dto.EnrollmentStudentDto(" +
                    "u.username," +
                    "e.enrollmentDate)" +
                    "FROM Enrollment as e " +
                    "JOIN User as u on u.id =e.student.id "+
                    "WHERE (:courseId is null or e.course.id =:courseId )"
    )
    List<EnrollmentStudentDto> getEnrollmentStudentByCourseId(Long courseId);

    List<Enrollment> findByCourseId(Long courseId);

    List<Enrollment> findByStudent(User student);
    boolean existsByStudentAndCourse(User student, Course course);
}

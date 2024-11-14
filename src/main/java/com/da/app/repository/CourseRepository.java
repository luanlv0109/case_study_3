package com.da.app.repository;

import com.da.app.domain.Course;
import com.da.app.dto.CourseDTO;
import com.da.app.dto.CourseManageDto;
import com.da.app.dto.DetailSourceDto;
import com.da.app.dto.ReportCourseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findById(Long id);
    @Query(
        "SELECT new com.da.app.dto.CourseManageDto(" +
                "c.id," +
                "c.courseName, " +
                "c.description," +
                "c.maxStudents," +
                "c.enrolledStudents," +
                "c.startDate," +
                "c.endDate) " +

                "FROM Course  as c "
    )
    Page<CourseManageDto> pageFilterCourse(
            Pageable pageable
    );

    @Query(
            "SELECT new com.da.app.dto.CourseManageDto(" +
                    "c.id," +
                    "c.courseName, " +
                    "c.description," +
                    "c.maxStudents," +
                    "c.enrolledStudents," +
                    "c.startDate," +
                    "c.endDate) " +

                    "FROM Course  as c "
    )
    List<CourseManageDto> getFilterCourse(
    );

    @Query(
            "SELECT new com.da.app.dto.CourseDTO(" +
                    "c.id," +
                    "c.courseName, " +
                    "c.description, " +
                    "c.maxStudents, " +
                    "c.enrolledStudents, " +
                    "c.startDate , " +
                    "case WHEN COUNT(e) > 0 THEN 1 ELSE 0 END)" +  // Kiểm tra có sinh viên đăng ký hay không
                    "FROM Course c " +
                    "LEFT JOIN Enrollment e on e.course.id = c.id AND e.student.id = :studentId " +  // LEFT JOIN để lấy tất cả các khóa học, kể cả khóa học không có sinh viên
                    "WHERE (1=1) " +
                    "AND c.startDate  >= CURRENT_DATE " +  // Nếu studentId là null, lấy tất cả các khóa học
                    "GROUP BY c.id, c.courseName, c.description, c.maxStudents, c.enrolledStudents"
    )
    Page<CourseDTO> pageFilterCourseForStudent(
            @Param("studentId") Long studentId,
            Pageable pageable
    );

    @Query(
            "SELECT new com.da.app.dto.CourseDTO(" +
                    "c.id, " +
                    "c.courseName, " +
                    "c.description, " +
                    "c.maxStudents, " +
                    "c.enrolledStudents, " +
                    "CASE WHEN COUNT(e) > 0 THEN 1 ELSE 0 END) " +
                    "FROM Course c " +
                    "LEFT JOIN Enrollment e on e.course.id = c.id  " +
                    "WHERE (:studentId is null or e.student.id = :studentId ) " +
                    "GROUP BY c.id, c.courseName, c.description, c.maxStudents, c.enrolledStudents"
    )
    Page<CourseDTO> PageFilterCoursesByStudentId(@Param("studentId") Long studentId, Pageable pageable);

    @Query(
            "SELECT new com.da.app.dto.DetailSourceDto(" +
                    "c.id, " +
                    "c.courseName, " +
                    "c.description, " +
                    "c.maxStudents, " +
                    "c.enrolledStudents, " +
                    "c.startDate," +
                    "c.endDate) " +
                    "FROM Course c " +
                    "WHERE (:CourseId is null or c.id = :CourseId ) " // Nếu studentId là null, lấy tất cả các khóa học
    )
    DetailSourceDto getDetailCourseByCourseId(@Param("CourseId") Long CourseId );

    @Query("SELECT new com.da.app.dto.ReportCourseDto(" +
            "c.courseName," +
            " COUNT(e.student) )" +
            "FROM Course c " +
            "LEFT JOIN Enrollment e ON e.course = c " +
            "WHERE e.enrollmentDate BETWEEN :startDate AND :endDate " +
            "GROUP BY c.courseName")
    List<ReportCourseDto> findCoursesWithEnrollmentCount(@Param("startDate") LocalDateTime startDate,
                                                         @Param("endDate") LocalDateTime endDate);

    List<Course> findByStartDateAfter(LocalDate date);

}

package com.da.app.service.course;

import com.da.app.dto.CourseDto;
import com.da.app.dto.CourseInput;
import com.da.app.dto.CourseManageDto;
import com.da.app.dto.DetailSourceDto;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface ICourseService {
    Page<CourseManageDto> filterCourse(int pageSize , int pageIndex);

    Page<CourseDto> filterCourseForStudent(int pageSize , int pageIndex , Long studentId);
    Page<CourseDto> filterCourseByStudentId(int pageSize , int pageIndex , Long studentId);

    DetailSourceDto getDetailSourceByCourseID(Long id);

    boolean registerStudentForCourse(Long studentId , Long courseId);

    boolean unregisterStudentFromCourse(Long studentId, Long courseId);
    boolean deleteCourse(Long id);

    boolean updateCourse(Long courseId, CourseInput courseInput);

    @Transactional
    boolean createCourse(CourseInput courseInput);
}

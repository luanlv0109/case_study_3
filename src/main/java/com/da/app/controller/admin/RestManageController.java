package com.da.app.controller.admin;

import com.da.app.dto.CourseInput;
import com.da.app.dto.DetailSourceDto;
import com.da.app.service.course.ICourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin")
@Slf4j

public class RestManageController {

    @Autowired
    private ICourseService courseService;

    @GetMapping("/course/{id}")
    public ResponseEntity<DetailSourceDto> getCourseDetail(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getDetailSourceByCourseID(id) ) ;
    }

    @DeleteMapping("course/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id) {
        boolean isDeleted = courseService.deleteCourse(id);

        if (isDeleted) {
            return ResponseEntity.ok("Xóa khóa học thành công.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy khóa học.");
        }
    }

    @PutMapping("/course/{id}")
    public ResponseEntity<String> updateCourse(@PathVariable Long id,  @Valid @RequestBody CourseInput updatedCourseInput) {
        boolean isUpdated = courseService.updateCourse(id, updatedCourseInput);

        if (isUpdated) {
            return ResponseEntity.ok("Cập nhật khóa học thành công.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy khóa học để cập nhật.");
        }
    }

    @PostMapping("/course")
    public ResponseEntity<String> createCourse(@Valid @RequestBody CourseInput courseInput) {
        boolean isCreated = courseService.createCourse(courseInput);

        if (isCreated) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Tạo khóa học thành công.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Thông tin khóa học không hợp lệ.");
        }
    }

}

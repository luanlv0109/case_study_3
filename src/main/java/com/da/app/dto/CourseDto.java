package com.da.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDto {
    private Long id;
    private String name;
    private String description;
    private int maxStudents;
    private int enrolledStudents;
    private int status;
    private String statusCourse;
    private int registered;

    public CourseDto(Long id, String name, String description, int maxStudents, int enrolledStudents , LocalDate date , int registered) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.maxStudents = maxStudents;
        this.enrolledStudents = enrolledStudents;
        this.status =  this.enrolledStudents == this.maxStudents ? 0 : 1;
        this.registered = registered;
        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), date);

        // Gán giá trị cho statusCourse
        if (daysRemaining > 0) {
            this.statusCourse = "Cách thời gian bắt đầu còn: " + daysRemaining + " ngày";
        } else if (daysRemaining == 0) {
            this.statusCourse = "Khóa học bắt đầu hôm nay!";
        } else {
            this.statusCourse = "Khóa học đã bắt đầu " + Math.abs(daysRemaining) + " ngày trước";
        }
    }

    public CourseDto(Long id, String name, String description, int maxStudents, int enrolledStudents , int registered ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.maxStudents = maxStudents;
        this.enrolledStudents = enrolledStudents;
        this.status =  this.enrolledStudents == this.maxStudents ? 0 : 1;
        this.registered = registered;
    }

}

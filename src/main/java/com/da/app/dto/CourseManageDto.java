package com.da.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseManageDto {
    private Long id;
    private String name;
    private String description;
    private int maxStudents;
    private int enrolledStudents;
    private String status;

    public CourseManageDto(Long id, String name, String description, int maxStudents, int enrolledStudents , LocalDate start , LocalDate end) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.maxStudents = maxStudents;
        this.enrolledStudents = enrolledStudents;
        LocalDate currentDate = LocalDate.now();
        if (currentDate.isBefore(start)) {
            this.status = "Chuẩn bị bắt đầu";
        } else if (currentDate.isAfter(end)) {
            this.status = "Đã kết thúc";
        } else {
            this.status = "Đang diễn ra";
        }
    }
}


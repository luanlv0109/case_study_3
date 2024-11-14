package com.da.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailSourceDto {
    private Long id;
    private String name;
    private String description;
    private int maxStudents;
    private int enrolledStudents;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<EnrollmentStudentDto> enrollmentStudents;

    public DetailSourceDto(Long id, String name, String description, int maxStudents, int enrolledStudents,LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.maxStudents = maxStudents;
        this.enrolledStudents = enrolledStudents;
        LocalDate currentDate = LocalDate.now();
        if (currentDate.isBefore(startDate)) {
            this.status = "Chuẩn bị bắt đầu";
        } else if (currentDate.isAfter(endDate)) {
            this.status = "Đã kết thúc";
        } else {
            this.status = "Đang diễn ra";
        }        this.startDate = startDate;
        this.endDate = endDate;
    }
}

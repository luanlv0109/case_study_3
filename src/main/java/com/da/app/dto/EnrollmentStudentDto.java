package com.da.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentStudentDto {
    private String username;
    private LocalDate enrollmentDate;

    public EnrollmentStudentDto(String username, LocalDateTime enrollmentDate) {
        this.username = username;
        this.enrollmentDate = enrollmentDate.toLocalDate();
    }
}

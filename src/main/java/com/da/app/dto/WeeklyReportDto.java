package com.da.app.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyReportDto {
    private Long id;
    private String name;
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    private String reportPath;
    private LocalDateTime createdAt;
    private String status ;
    private String type;
    private String creator;

    public WeeklyReportDto(Long id, String name, LocalDate weekStartDate, LocalDate weekEndDate, String reportPath, LocalDateTime createdAt , String type , String creator) {
        this.id = id;
        this.name = name;
        this.weekStartDate = weekStartDate;
        this.weekEndDate = weekEndDate;
        this.reportPath = reportPath;
        this.createdAt = createdAt;
        this.status = "Hoàn thành";
        this.type = type;
        this.creator = creator;
    }
}
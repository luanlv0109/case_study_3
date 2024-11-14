package com.da.app.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;
    private String creator;
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;

    private String reportPath;  // Report could be CSV, JSON, or text data

    private LocalDateTime createdAt;
}

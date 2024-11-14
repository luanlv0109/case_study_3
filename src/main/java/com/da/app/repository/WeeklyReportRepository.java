package com.da.app.repository;


import com.da.app.domain.WeeklyReport;
import com.da.app.dto.CourseManageDto;
import com.da.app.dto.WeeklyReportDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WeeklyReportRepository extends JpaRepository<WeeklyReport, Long> {
    @Query(
            "SELECT new com.da.app.dto.WeeklyReportDto(" +
                    "c.id," +
                    "c.name, " +
                    "c.weekStartDate," +
                    "c.weekEndDate," +
                    "c.reportPath," +
                    "c.createdAt," +
                    "c.type," +
                    "c.creator) " +

                    "FROM WeeklyReport  as c "
    )
    Page<WeeklyReportDto> pageFilterWeeklyReport(
            Pageable pageable
    );
}
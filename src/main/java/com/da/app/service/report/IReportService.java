package com.da.app.service.report;

import com.da.app.dto.WeeklyReportDto;
import org.springframework.data.domain.Page;

public interface IReportService {
    Page<WeeklyReportDto> getWeeklyReports(int pageSize , int pageIndex);

}

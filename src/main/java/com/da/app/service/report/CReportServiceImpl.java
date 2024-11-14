package com.da.app.service.report;

import com.da.app.dto.WeeklyReportDto;
import com.da.app.repository.WeeklyReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CReportServiceImpl implements IReportService {
    @Autowired
    private WeeklyReportRepository weeklyReportRepository;
    @Override
    public Page<WeeklyReportDto> getWeeklyReports(int pageSize, int pageIndex) {
        return weeklyReportRepository.pageFilterWeeklyReport(
                PageRequest.of(
                        pageIndex,
                        pageSize
                )
        );
    }
}

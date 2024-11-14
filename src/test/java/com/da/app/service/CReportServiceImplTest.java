package com.da.app.service;

import com.da.app.dto.WeeklyReportDto;
import com.da.app.repository.WeeklyReportRepository;
import com.da.app.service.report.CReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CReportServiceImplTest {

    @Mock
    private WeeklyReportRepository weeklyReportRepository;

    @InjectMocks
    private CReportServiceImpl reportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetWeeklyReports() {
        // Arrange
        int pageSize = 10;
        int pageIndex = 0;

        WeeklyReportDto reportDto = new WeeklyReportDto();  // Use an actual constructor if needed
        Page<WeeklyReportDto> page = new PageImpl<>(Collections.singletonList(reportDto));

        when(weeklyReportRepository.pageFilterWeeklyReport(PageRequest.of(pageIndex, pageSize)))
                .thenReturn(page);

        // Act
        Page<WeeklyReportDto> result = reportService.getWeeklyReports(pageSize, pageIndex);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());  // Check that the result contains one report
        assertEquals(reportDto, result.getContent().get(0));  // Verify the report content
        verify(weeklyReportRepository).pageFilterWeeklyReport(PageRequest.of(pageIndex, pageSize));  // Verify interaction with the repository
    }
}

package com.da.app.component;

import com.da.app.dto.ReportCourseDto;
import org.springframework.batch.item.ItemProcessor;

public class CourseReportProcessor implements ItemProcessor<ReportCourseDto, ReportCourseDto> {

    @Override
    public ReportCourseDto process(ReportCourseDto item) throws Exception {
        return item;
    }
}

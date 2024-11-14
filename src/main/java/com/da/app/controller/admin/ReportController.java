package com.da.app.controller.admin;

import com.da.app.dto.CourseManageDto;
import com.da.app.dto.WeeklyReportDto;
import com.da.app.service.report.IReportService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@Slf4j
public class ReportController {
    @Autowired
    private IReportService reportService;

    @GetMapping("/weekly-reports")
    public String pageFilterWeeklyReports(
            HttpSession session, Model model,
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "12") int pageSize) {
        Page<WeeklyReportDto> coursePage =  reportService.getWeeklyReports(pageSize, pageIndex);
        model.addAttribute("weekyReports", coursePage.getContent());
        model.addAttribute("currentPage", pageIndex);
        model.addAttribute("totalPages", coursePage.getTotalPages());
        model.addAttribute("pageSize", pageSize);
        return "admin/report";
    }
}

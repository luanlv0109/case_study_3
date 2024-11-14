package com.da.app.controller.admin;

import com.da.app.dto.CourseManageDto;
import com.da.app.service.course.ICourseService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@Slf4j

public class ManagerCourseController {

    @Autowired
    private ICourseService courseService;

    @GetMapping("/course-manage")
    public String pageFilterCourseForStudent(
            HttpSession session, Model model,
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "12") int pageSize) {
        Page<CourseManageDto> coursePage =  courseService.filterCourse(pageSize, pageIndex);
        model.addAttribute("courses", coursePage.getContent());
        model.addAttribute("currentPage", pageIndex);
        model.addAttribute("totalPages", coursePage.getTotalPages());
        model.addAttribute("pageSize", pageSize);
        return "admin/manage-course";
    }
}

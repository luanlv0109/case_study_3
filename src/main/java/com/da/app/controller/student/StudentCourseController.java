package com.da.app.controller.student;

import com.da.app.dto.CourseDto;
import com.da.app.service.course.ICourseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/student")
public class StudentCourseController {

    @Autowired
    private ICourseService courseService;

    @GetMapping("/courses")
    public String pageFilterCourseForStudent(
            HttpSession session, Model model,
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "12") int pageSize) {
        Long studentId = (Long) session.getAttribute("userId");
        Page<CourseDto> coursePage =  courseService.filterCourseForStudent(pageSize, pageIndex, studentId);
        model.addAttribute("courses", coursePage.getContent());
        model.addAttribute("currentPage", pageIndex);
        model.addAttribute("totalPages", coursePage.getTotalPages());
        model.addAttribute("pageSize", pageSize);


        return "student/course-student";
    }

    @GetMapping("/my-courses")
    public String pageFilterCourseByStudentId(
            HttpSession session, Model model,
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "12") int pageSize) {
        Long studentId = (Long) session.getAttribute("userId");
        Page<CourseDto> coursePage =  courseService.filterCourseByStudentId(pageSize, pageIndex, studentId);
        model.addAttribute("courses", coursePage.getContent());
        model.addAttribute("currentPage", pageIndex);
        model.addAttribute("totalPages", coursePage.getTotalPages());
        model.addAttribute("pageSize", pageSize);


        return "student/course-my-student";
    }

    @GetMapping("/register/{courseId}")
    public String registerCourse(@PathVariable Long courseId, HttpSession session, Model model) {
        Long studentId = (Long) session.getAttribute("userId");

        if (studentId == null) {
            model.addAttribute("message", "Vui lòng đăng nhập trước khi đăng ký khóa học.");
            return "redirect:/login";  // Redirect đến trang đăng nhập
        }

        boolean isRegistered = courseService.registerStudentForCourse(studentId, courseId);

        if (isRegistered) {
            model.addAttribute("message", "Đăng ký khóa học thành công!");
        } else {
            model.addAttribute("message", "Không thể đăng ký khóa học (khóa học đầy hoặc bạn đã đăng ký rồi).");
        }

        return "redirect:/student/courses";
    }

    @GetMapping("/unregister/{courseId}")
    public String unregisterCourse(@PathVariable Long courseId, HttpSession session, Model model , RedirectAttributes redirectAttributes) {
        Long studentId = (Long) session.getAttribute("userId");

        if (studentId == null) {
            model.addAttribute("message", "Vui lòng đăng nhập trước khi hủy đăng ký khóa học.");
            return "redirect:/login";  // Redirect đến trang đăng nhập
        }

        boolean isUnregistered = courseService.unregisterStudentFromCourse(studentId, courseId);

        if (isUnregistered) {
            redirectAttributes.addFlashAttribute("message", "Hủy đăng ký khóa học thành công!");
            return "redirect:/student/my-courses";
        } else {
            redirectAttributes.addFlashAttribute("message", "Không thể hủy đăng ký khóa học (khóa học đã bắt đầu hoặc bạn chưa đăng ký khóa học).");
            return "redirect:/student/my-courses?error=true";
        }
    }
}

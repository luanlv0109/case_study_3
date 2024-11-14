package com.da.app.controller.author;

import com.da.app.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {
    @Autowired
    private IUserService userService;

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "author/register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               Model model) {
        try {
            userService.registerNewUser(username, password, "STUDENT");
            model.addAttribute("message", "Đăng ký thành công!");
            return "author/login";
        } catch (Exception e) {
            model.addAttribute("error", "Đăng ký không thành công. Vui lòng thử lại.");
            return "author/register";
        }
    }
}

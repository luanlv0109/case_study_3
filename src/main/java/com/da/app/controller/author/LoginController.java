package com.da.app.controller.author;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage() {
        return "author/login";
    }

}

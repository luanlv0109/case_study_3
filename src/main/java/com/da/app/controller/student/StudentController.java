package com.da.app.controller.student;

import com.da.app.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student")
@Slf4j
public class StudentController {


    @GetMapping("/student-view")
    public String adminView(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.warn("Authentication is null");
        } else {
            log.info("Authentication exists with name: {}", authentication.getName());
        }

        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();

            UserDTO userDTO = new UserDTO(username);
            userDTO.setAdmin(true);

            log.info("UserDTO created with username: {}", userDTO.getUsername());
            log.info("UserDTO is admin: {}", userDTO.isAdmin());

            model.addAttribute("user", userDTO);
            log.info("UserDTO added to model with attributes: {}", model.asMap());
        } else {
            log.error("Failed to retrieve user details from authentication");
        }
        return "student/student";
    }
}

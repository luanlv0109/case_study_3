package com.da.app.config;

import com.da.app.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component("customAuthenticationSuccessHandlerBean")
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();
        String role = user.getAuthorities().toArray()[0].toString();
com.da.app.domain.User ExitUser = (com.da.app.domain.User) userRepository.findByUsername(user.getUsername())
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        HttpSession session = request.getSession();
        session.setAttribute("username", user.getUsername());
        session.setAttribute("userId", ExitUser.getId());

        if (role.equals("ROLE_ADMIN")) {
            session.setAttribute("isAdmin", true);
            response.sendRedirect("/admin/admin-view");
        } else if (role.equals("ROLE_STUDENT")) {
            session.setAttribute("isAdmin", false);
            response.sendRedirect("/student/student-view");
        } else {
            response.sendRedirect("/");
        }
    }
}

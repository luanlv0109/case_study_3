package com.da.app.service.user;

import com.da.app.domain.User;
import com.da.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void registerNewUser(String username, String password, String role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Tên người dùng đã tồn tại!");
        }

        // Mã hóa mật khẩu trước khi lưu
        String encodedPassword = passwordEncoder.encode(password);

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(encodedPassword);
        newUser.setRole(role);

        userRepository.save(newUser);
    }
}

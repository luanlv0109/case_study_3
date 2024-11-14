package com.da.app.service;

import com.da.app.domain.User;
import com.da.app.repository.UserRepository;
import com.da.app.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private String username = "testUser";
    private String password = "password123";
    private String role = "USER";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterNewUser_Success() {
        // Arrange
        when(userRepository.existsByUsername(username)).thenReturn(false);  // Username doesn't exist
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword123");  // Mock password encoding

        // Act
        userService.registerNewUser(username, password, role);

        // Assert
        verify(userRepository).existsByUsername(username);  // Verify check if username exists
        verify(userRepository).save(any(User.class));  // Verify that save is called with a User object
        verify(passwordEncoder).encode(password);  // Verify that the password is encoded
    }

    @Test
    void testRegisterNewUser_UsernameExists() {
        // Arrange
        when(userRepository.existsByUsername(username)).thenReturn(true);  // Username already exists

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                userService.registerNewUser(username, password, role)
        );
        assertEquals("Tên người dùng đã tồn tại!", thrown.getMessage());  // Verify the exception message
    }
}

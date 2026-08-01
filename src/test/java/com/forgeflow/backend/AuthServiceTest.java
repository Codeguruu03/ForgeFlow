package com.forgeflow.backend;

import com.forgeflow.backend.model.User;
import com.forgeflow.backend.repository.UserRepository;
import com.forgeflow.backend.service.AuthService;
import com.forgeflow.shared.dto.JwtAuthResponse;
import com.forgeflow.shared.dto.LoginRequest;
import com.forgeflow.shared.dto.RegisterRequest;
import com.forgeflow.shared.dto.UserDto;
import com.forgeflow.shared.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // DataInitializer already seeds admin, developer, viewer
    }

    @Test
    void testAdminLogin() {
        LoginRequest loginRequest = new LoginRequest("admin", "admin123");
        JwtAuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("admin", response.getUsername());
        assertEquals(UserRole.ADMINISTRATOR, response.getRole());
    }

    @Test
    void testUserRegistration() {
        RegisterRequest registerRequest = new RegisterRequest("newdev", "pass123", "newdev@forgeflow.internal", UserRole.DEVELOPER);
        UserDto registeredUser = authService.register(registerRequest);

        assertNotNull(registeredUser);
        assertEquals("newdev", registeredUser.getUsername());
        assertEquals(UserRole.DEVELOPER, registeredUser.getRole());

        // Verify login with newly registered user
        LoginRequest loginRequest = new LoginRequest("newdev", "pass123");
        JwtAuthResponse response = authService.login(loginRequest);
        assertNotNull(response.getToken());
    }
}

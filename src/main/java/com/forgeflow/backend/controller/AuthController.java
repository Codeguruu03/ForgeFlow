package com.forgeflow.backend.controller;

import com.forgeflow.backend.service.AuthService;
import com.forgeflow.shared.dto.JwtAuthResponse;
import com.forgeflow.shared.dto.LoginRequest;
import com.forgeflow.shared.dto.RegisterRequest;
import com.forgeflow.shared.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginRequest loginRequest) {
        JwtAuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody RegisterRequest registerRequest) {
        UserDto registeredUser = authService.register(registerRequest);
        return ResponseEntity.ok(registeredUser);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        UserDto currentUser = authService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(currentUser);
    }
}

package com.forgeflow.shared.dto;

import com.forgeflow.shared.enums.UserRole;

public class JwtAuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private String username;
    private String email;
    private UserRole role;

    public JwtAuthResponse() {}

    public JwtAuthResponse(String token, String username, String email, UserRole role) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}

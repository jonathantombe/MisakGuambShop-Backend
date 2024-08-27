package com.misakguambshop.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginDto {
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    // Constructor
    public UserLoginDto() {}

    public UserLoginDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters y Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
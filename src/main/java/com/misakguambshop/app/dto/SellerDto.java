package com.misakguambshop.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SellerDto {
    @NotBlank
    @Size(min = 3, max = 100)
    private String fullName;

    @NotBlank
    @Size(max = 100)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    @NotBlank
    @Size(max = 15)
    private String phone;

    @NotBlank
    @Size(max = 100)
    private String companyName;

    @Size(max = 500)
    private String description;

    @NotBlank
    @Size(max = 50)
    private String city;
}
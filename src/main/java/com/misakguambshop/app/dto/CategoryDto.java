package com.misakguambshop.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.bind.annotation.CrossOrigin;

@Data
@CrossOrigin(origins = "https://misak-guamb-shop-front-git-develop-my-team-f83432a3.vercel.app")
public class CategoryDto {
    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name must be less than 50 characters")
    private String name;

    private String description;
}

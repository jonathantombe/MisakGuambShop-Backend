package com.misakguambshop.app.dto;

import jakarta.validation.constraints.*;
import org.springframework.web.bind.annotation.CrossOrigin;

public class ReviewDto {

    private Long id;

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productId;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;

    @Min(value = 1, message = "La calificación debe ser al menos 1")
    @Max(value = 5, message = "La calificación no puede ser mayor que 5")
    private int rating;

    @Size(max = 500, message = "El comentario no debe exceder los 500 caracteres")
    private String comment;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

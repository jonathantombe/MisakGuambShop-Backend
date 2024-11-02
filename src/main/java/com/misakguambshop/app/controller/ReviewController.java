package com.misakguambshop.app.controller;

import com.misakguambshop.app.dto.ReviewDto;
import com.misakguambshop.app.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ReviewDto>> getReviewsByProduct(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId, page, size));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ReviewDto>> getReviewsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId, page, size));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<ReviewDto> createReview(@Valid @RequestBody ReviewDto reviewDto) {
        return ResponseEntity.status(201).body(reviewService.createReview(reviewDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<ReviewDto> updateReview(@PathVariable Long id, @Valid @RequestBody ReviewDto reviewDto) {
        return ResponseEntity.ok(reviewService.updateReview(id, reviewDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<String> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok("La reseña ha sido eliminada exitosamente.");
    }
}

package com.misakguambshop.app.controller;

import com.misakguambshop.app.dto.OrderDetailsDto;
import com.misakguambshop.app.service.OrderDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order-details")
public class OrderDetailsController {

    @Autowired
    private OrderDetailsService orderDetailsService;

    @GetMapping
    public ResponseEntity<List<OrderDetailsDto>> getAllOrderDetails() {
        return ResponseEntity.ok(orderDetailsService.getAllOrderDetails());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailsDto> getOrderDetailsById(@PathVariable Long id) {
        return ResponseEntity.ok(orderDetailsService.getOrderDetailsById(id));
    }

    @PostMapping
    public ResponseEntity<OrderDetailsDto> createOrderDetails(@Valid @RequestBody OrderDetailsDto orderDetailsDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDetailsService.createOrderDetails(orderDetailsDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDetailsDto> updateOrderDetails(@PathVariable Long id, @Valid @RequestBody OrderDetailsDto orderDetailsDto) {
        return ResponseEntity.ok(orderDetailsService.updateOrderDetails(id, orderDetailsDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrderDetailsDto> patchOrderDetails(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(orderDetailsService.patchOrderDetails(id, updates));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderDetails(@PathVariable Long id) {
        orderDetailsService.deleteOrderDetails(id);
        return ResponseEntity.noContent().build();
    }
}

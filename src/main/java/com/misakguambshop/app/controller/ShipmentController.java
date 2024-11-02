package com.misakguambshop.app.controller;


import com.misakguambshop.app.dto.ShipmentDto;
import com.misakguambshop.app.service.ShipmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    @Autowired
    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SELLER')")
    public ResponseEntity<Map<String, Object>> getAllShipments() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", shipmentService.getAllShipments());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<Map<String, Object>> getShipmentById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", shipmentService.getShipmentById(id));
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Map<String, Object>> createShipment(@Valid @RequestBody ShipmentDto shipmentDto) {
        ShipmentDto createdShipment = shipmentService.createShipment(shipmentDto);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Dirección guardada exitosamente");
        response.put("data", createdShipment);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<Map<String, Object>> updateShipment(
            @PathVariable Long id,
            @Valid @RequestBody ShipmentDto shipmentDto) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Dirección actualizada exitosamente");
        response.put("data", shipmentService.updateShipment(id, shipmentDto));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<Map<String, Object>> partialUpdateShipment(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Dirección actualizada parcialmente");
        response.put("data", shipmentService.partialUpdateShipment(id, updates));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SELLER')")
    public ResponseEntity<Map<String, Object>> deleteShipment(@PathVariable Long id) {
        shipmentService.deleteShipment(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Dirección eliminada exitosamente");
        return ResponseEntity.ok(response);
    }
}
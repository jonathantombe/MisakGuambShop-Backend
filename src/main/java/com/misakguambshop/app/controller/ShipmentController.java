package com.misakguambshop.app.controller;

import com.misakguambshop.app.dto.ShipmentDto;
import com.misakguambshop.app.service.ShipmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SELLER') or hasAuthority('USER')")
    public ResponseEntity<List<ShipmentDto>> getAllShipments() {
        return ResponseEntity.ok(shipmentService.getAllShipments());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<ShipmentDto> getShipmentById(@PathVariable Long id) {
        return ResponseEntity.ok(shipmentService.getShipmentById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SELLER') or hasAuthority('USER')")
    public ResponseEntity<ShipmentDto> createShipment(@Valid @RequestBody ShipmentDto shipmentDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shipmentService.createShipment(shipmentDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SELLER') or hasAuthority('USER')")
    public ResponseEntity<ShipmentDto> updateShipment(@PathVariable Long id, @Valid @RequestBody ShipmentDto shipmentDto) {
        return ResponseEntity.ok(shipmentService.updateShipment(id, shipmentDto));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SELLER') or hasAuthority('USER')")
    public ResponseEntity<Void> deleteShipment(@PathVariable Long id) {
        shipmentService.deleteShipment(id);
        return ResponseEntity.noContent().build();
    }

}
package com.misakguambshop.app.model;

public enum ShipmentStatus {
    PENDING,
    IN_TRANSIT,
    DELIVERED,
    RETURNED;

    public static ShipmentStatus fromString(String status) {
        try {
            return ShipmentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Estado de envío no válido: " + status);
        }
    }

}
package com.misakguambshop.app.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum OrderStatus {
    @JsonProperty("pending")
    PENDING,
    @JsonProperty("processing")
    PROCESSING,
    @JsonProperty("shipped")
    SHIPPED,
    @JsonProperty("delivered")
    DELIVERED,
    @JsonProperty("canceled")
    CANCELED;

    @JsonCreator
    public static OrderStatus forValue(String value) {
        try {
            return OrderStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown value for OrderStatus: " + value, e);
        }
    }
}

package com.misakguambshop.app.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class OrderStatusDeserializer extends JsonDeserializer<OrderStatus> {

    @Override
    public OrderStatus deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        String value = p.getText();
        if (value == null || value.isEmpty()) {
            return null;
        }
        return OrderStatus.forValue(value);
    }
}

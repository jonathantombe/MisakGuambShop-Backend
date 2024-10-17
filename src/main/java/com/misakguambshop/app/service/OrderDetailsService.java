package com.misakguambshop.app.service;

import com.misakguambshop.app.dto.OrderDetailsDto;
import com.misakguambshop.app.exception.ResourceNotFoundException;
import com.misakguambshop.app.model.OrderDetails;
import com.misakguambshop.app.model.Order;
import com.misakguambshop.app.model.Product;
import com.misakguambshop.app.repository.OrderDetailsRepository;
import com.misakguambshop.app.repository.OrderRepository;
import com.misakguambshop.app.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderDetailsService {

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<OrderDetailsDto> getAllOrderDetails() {
        return orderDetailsRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public OrderDetailsDto getOrderDetailsById(Long id) {
        OrderDetails orderDetails = orderDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order Details not found with ID: " + id));
        return convertToDto(orderDetails);
    }

    @Transactional
    public OrderDetailsDto createOrderDetails(OrderDetailsDto orderDetailsDto) {
        Order order = orderRepository.findById(orderDetailsDto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        Product product = productRepository.findById(orderDetailsDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        OrderDetails orderDetails = convertToEntity(orderDetailsDto, order, product);
        orderDetailsRepository.save(orderDetails);
        return convertToDto(orderDetails);
    }

    @Transactional
    public OrderDetailsDto updateOrderDetails(Long id, OrderDetailsDto orderDetailsDto) {
        OrderDetails existingOrderDetails = orderDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order Details not found with ID: " + id));

        Order order = orderRepository.findById(orderDetailsDto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        Product product = productRepository.findById(orderDetailsDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        existingOrderDetails.setOrder(order);
        existingOrderDetails.setProduct(product);
        existingOrderDetails.setQuantity(orderDetailsDto.getQuantity());
        existingOrderDetails.setUnitPrice(orderDetailsDto.getUnitPrice());

        existingOrderDetails.calculateSubtotal();

        OrderDetails updatedOrderDetails = orderDetailsRepository.save(existingOrderDetails);
        return convertToDto(updatedOrderDetails);
    }

    @Transactional
    public OrderDetailsDto patchOrderDetails(Long id, Map<String, Object> updates) {
        OrderDetails existingOrderDetails = orderDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order Details not found with ID: " + id));

        updates.forEach((key, value) -> {
            switch (key) {
                case "quantity":
                    existingOrderDetails.setQuantity((Integer) value);
                    break;
                case "unitPrice":
                    existingOrderDetails.setUnitPrice(new BigDecimal(value.toString()));
                    break;

                default:
                    throw new IllegalArgumentException("Invalid field: " + key);
            }
        });

        existingOrderDetails.calculateSubtotal();
        orderDetailsRepository.save(existingOrderDetails);
        return convertToDto(existingOrderDetails);
    }

    @Transactional
    public void deleteOrderDetails(Long id) {
        OrderDetails orderDetails = orderDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order Details not found with ID: " + id));
        orderDetailsRepository.delete(orderDetails);
    }

    private OrderDetailsDto convertToDto(OrderDetails orderDetails) {
        OrderDetailsDto dto = new OrderDetailsDto();
        dto.setId(orderDetails.getId());
        dto.setOrderId(orderDetails.getOrder().getId());
        dto.setProductId(orderDetails.getProduct().getId());
        dto.setQuantity(orderDetails.getQuantity());
        dto.setUnitPrice(orderDetails.getUnitPrice());
        dto.setSubtotal(orderDetails.getSubtotal());
        return dto;
    }

    private OrderDetails convertToEntity(OrderDetailsDto dto, Order order, Product product) {
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setOrder(order);
        orderDetails.setProduct(product);
        orderDetails.setQuantity(dto.getQuantity());
        orderDetails.setUnitPrice(dto.getUnitPrice());
        orderDetails.calculateSubtotal();
        return orderDetails;
    }
}

package com.misakguambshop.app.service;

import com.misakguambshop.app.dto.OrderDto;
import com.misakguambshop.app.exception.ResourceNotFoundException;
import com.misakguambshop.app.model.Order;
import com.misakguambshop.app.model.OrderStatus;
import com.misakguambshop.app.model.PaymentStatus;
import com.misakguambshop.app.model.User;
import com.misakguambshop.app.repository.OrderRepository;
import com.misakguambshop.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    public List<OrderDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return convertToDto(order);
    }

    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {
        User user = userRepository.findById(orderDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(orderDto.getOrderDate() != null ? orderDto.getOrderDate() : LocalDateTime.now());
        order.setStatus(orderDto.getStatus());
        order.setPaymentStatus(orderDto.getPaymentStatus());
        order.setPaymentReference(orderDto.getPaymentReference());
        order.setTotalAmount(orderDto.getTotalAmount());
        Order savedOrder = orderRepository.save(order);
        return convertToDto(savedOrder);
    }

    @Transactional
    public OrderDto updateOrder(Long id, OrderDto orderDto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (orderDto.getOrderDate() != null) {
            order.setOrderDate(orderDto.getOrderDate());
        }

        order.setStatus(orderDto.getStatus());
        order.setPaymentStatus(orderDto.getPaymentStatus());
        order.setPaymentReference(orderDto.getPaymentReference());
        order.setTotalAmount(orderDto.getTotalAmount());

        Order updatedOrder = orderRepository.save(order);
        return convertToDto(updatedOrder);
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        orderRepository.delete(order);
    }

    @Transactional
    public OrderDto patchOrder(Long id, Map<String, Object> updates) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "orderDate":
                    if (value != null) {
                        order.setOrderDate(LocalDateTime.parse(value.toString()));
                    }
                    break;
                case "status":
                    if (value != null) {
                        try {
                            order.setStatus(OrderStatus.forValue(value.toString()));
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Invalid value for status: " + value, e);
                        }
                    }
                    break;
                case "paymentStatus":
                    if (value != null) {
                        order.setPaymentStatus(PaymentStatus.valueOf(value.toString()));
                    }
                    break;
                case "paymentReference":
                    order.setPaymentReference(value != null ? value.toString() : null);
                    break;
                case "totalAmount":
                    if (value != null) {
                        order.setTotalAmount(new BigDecimal(value.toString()));
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Invalid field: " + key);
            }
        });

        Order updatedOrder = orderRepository.save(order);
        return convertToDto(updatedOrder);
    }

    private OrderDto convertToDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setUserId(order.getUser().getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setPaymentReference(order.getPaymentReference());
        dto.setTotalAmount(order.getTotalAmount());
        return dto;
    }
}

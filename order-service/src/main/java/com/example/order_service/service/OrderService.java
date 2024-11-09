package com.example.order_service.service;

import com.example.order_service.dto.OrderDto;
import com.example.order_service.jpa.OrderEntity;
import jakarta.annotation.Nullable;

public interface OrderService {
    OrderDto createOrder(OrderDto orderDto);
    OrderDto ChangeQty(OrderDto orderDto, Integer qty);
    OrderDto getOrderByOrderId(String orderId);
    OrderDto getOrderByProductId(String productId);
    Iterable<OrderEntity> getOrderByUserId(String userId);
}

package com.example.gccoffee.repository;

import com.example.gccoffee.model.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order insert(Order order);

    Optional<Order> findById(UUID orderId);
}

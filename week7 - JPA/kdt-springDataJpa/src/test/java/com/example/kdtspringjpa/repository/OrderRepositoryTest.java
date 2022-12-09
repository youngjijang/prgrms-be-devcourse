package com.example.kdtspringjpa.repository;

import com.example.kdtspringjpa.domain.Order;
import com.example.kdtspringjpa.domain.OrderStatus;
import com.example.kdtspringjpa.domain.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@SpringBootTest
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @Test
    void test() {
        Order order = new Order(); // 준영속상태
        order.setUuid(UUID.randomUUID().toString());
        order.setOrderStatus(OrderStatus.OPENED);
        order.setCreatedBy("youngji");
        order.setCratedAt(LocalDateTime.now());

        orderRepository.save(order);

        Order findOrder = orderRepository.findById(order.getUuid()).get();
        List<Order> orders = orderRepository.findAll();

        var OpenOrder = orderRepository.findAllByOrderStatus(OrderStatus.OPENED);
    }
}
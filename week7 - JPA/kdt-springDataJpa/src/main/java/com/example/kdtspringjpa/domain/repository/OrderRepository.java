package com.example.kdtspringjpa.domain.repository;

import com.example.kdtspringjpa.domain.Order;
import com.example.kdtspringjpa.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findAllByOrderStatus(OrderStatus orderStatus);

    List<Order> findAllByOrderStatusOrderByOrderDatetime(OrderStatus orderStatus);

    @Query("SELECT o FROM Order AS o WHERE o.memo LIKE %?1%")
    Optional<Order> findByMemo(String memo);
}

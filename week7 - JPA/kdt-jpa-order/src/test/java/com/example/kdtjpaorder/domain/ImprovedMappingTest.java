package com.example.kdtjpaorder.domain;


import com.example.kdtjpaorder.domain.order.Food;
import com.example.kdtjpaorder.domain.order.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.example.kdtjpaorder.domain.order.OrderStatus.OPENED;

@Slf4j
@SpringBootTest
public class ImprovedMappingTest {

    @Autowired
    private EntityManagerFactory emf;

    @Test
    void inheritance_test() {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        Food food = new Food();
        food.setPrice(5000);
        food.setStockQuantity(100);
        food.setChef("백종원");

        entityManager.persist(food);
        transaction.commit();
        entityManager.clear();
        entityManager.find(Food.class, food.getId());

    }

    @Test
    void mapped_super_class_test() {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        Order order = new Order();
        order.setUuid(UUID.randomUUID().toString());
        order.setOrderDatetime(LocalDateTime.now());
        order.setOrderStatus(OPENED);
        order.setMemo("부재시 전화주세요.");

        //
        order.setCreatedBy("영지");

        entityManager.persist(order);
        transaction.commit();
        entityManager.clear();
        entityManager.find(Order.class, order.getUuid());

    }
}

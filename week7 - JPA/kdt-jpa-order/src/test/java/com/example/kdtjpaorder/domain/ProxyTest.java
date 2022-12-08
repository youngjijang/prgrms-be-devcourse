package com.example.kdtjpaorder.domain;

import com.example.kdtjpaorder.domain.order.Member;
import com.example.kdtjpaorder.domain.order.Order;
import com.example.kdtjpaorder.domain.order.OrderItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.example.kdtjpaorder.domain.order.OrderStatus.OPENED;

@SpringBootTest
@Slf4j
public class ProxyTest {

    @Autowired
    EntityManagerFactory emf;

    private String uuid = UUID.randomUUID().toString();

    @BeforeEach
    void setUp(){
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        Member member = new Member();
        member.setName("kanghonggu");
        member.setAddress("서울시 동작구(만) 움직이면 쏜다.");
        member.setAge(33);
        member.setNickName("guppy.kang");

        entityManager.persist(member);

        Order order = new Order();
        order.setUuid(uuid);
        order.setOrderDatetime(LocalDateTime.now());
        order.setOrderStatus(OPENED);
        order.setMemo("부재시 전화주세요.");
        order.setMember(member); // 외래키를 직접 지정

        entityManager.persist(order);

        transaction.commit();

    }

    @Test
    void proxy(){
        EntityManager entityManager = emf.createEntityManager();
        Order order = entityManager.find(Order.class,uuid);

        Member member = order.getMember();
        log.info("Member use before is-Loaded : {}",emf.getPersistenceUnitUtil().isLoaded(member)); // 사용하기전 member 객체는 proxy 객체 - false

        var orders = member.getOrders();
        log.info("Member use after is-Loaded : {}",emf.getPersistenceUnitUtil().isLoaded(member));
        log.info(orders.toString());
    }

    @Test
    void move_persist(){
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();
        Order order = entityManager.find(Order.class,uuid);


        OrderItem item = new OrderItem();
        item.setQuantity(10);
        item.setPrice(10000);

        order.addOrderItem(item);

        transaction.commit();

        entityManager.clear();

        transaction.begin();
        // 회원 조회 -> 회원의 주문 까지 조회
        Order order2 = entityManager.find(Order.class, uuid);
        order2.getOrderItems().remove(0); // orderItem를 제거한다. (고아객체 발생)

        transaction.commit();
    }

}

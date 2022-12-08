package com.example.kdtjpaorder.domain.parent;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class ParentTest {

    @Autowired
    private EntityManagerFactory emf;

    @Test
    void multi_key_test() {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        Parent parent = new Parent();
        parent.setId(new ParentId("id1","id2"));
        entityManager.persist(parent);

        transaction.commit();

        Parent entity = entityManager.find(Parent.class, new ParentId("id1", "id2"));
        log.info("{}, {}", entity.getId().getId2(), entity.getId().getId1());
    }

}
package com.example.kdtjpapractice.domain;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;

@Slf4j
@SpringBootTest
public class PersistenceContextTest {

//    @Autowired
//    CustomerRepository customerRepository;

    @Autowired
    EntityManagerFactory emf;

    @Test
    void 저장(){
        EntityManager em = emf.createEntityManager(); // 1)엔티티 매니저 생성

        EntityTransaction transaction = em.getTransaction(); // 2)트랜잭션 획득
        transaction.begin(); // 3)트랙잰셕 begin

        Customer customer = new Customer(); // 4-1)비영속
        customer.setId(1L);
        customer.setFirstName("honggu");
        customer.setLastName("kang");

        em.persist(customer); // 4-2)영속화

        transaction.commit(); // 5)트랜잭션 commit
        // 트랜잭션이 커밋이 되는 순간 쿼리가 수행된다. flush DB와 동기화가 된다.
    }

}

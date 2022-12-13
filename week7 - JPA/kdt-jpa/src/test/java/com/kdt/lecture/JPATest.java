package com.kdt.lecture;

import com.kdt.lecture.repository.CustomerRepository;
import com.kdt.lecture.repository.domain.CustomerEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

@Slf4j
@SpringBootTest
public class JPATest {

    @Autowired
    CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        customerRepository.deleteAll();
    }

    @Test
    void INSERT_TEST() {

        //Given
        CustomerEntity customer = new CustomerEntity();
        customer.setId(1L);
        customer.setFirstName("youngji");
        customer.setLastName("jang");

        //When
        customerRepository.save(customer);

        //then
        CustomerEntity entity = customerRepository.findById(1L).get();
        log.info("{} {}", entity.getFirstName(), entity.getLastName());
    }

    @Test
   // @Transactional // 영속석 컨텍스트 안에서 관리를 하겠다.
    void UPDATE_TEST(){
        //Given
        CustomerEntity customer = new CustomerEntity();
        customer.setId(1L);
        customer.setFirstName("youngji");
        customer.setLastName("jang");
        customerRepository.save(customer);

        //When
        CustomerEntity entity = customerRepository.findById(1L).get();

        //then
        entity.setFirstName("guppy");
        entity.setLastName("kang");
        // 따로 저장을 하지 않아도 영속석 컨텍스트에 의해 더티 체킹하고 있어 감지를 하고 속성이 변경되면 자동적으로 update 쿼리가 날라간다.
        CustomerEntity updated = customerRepository.findById(1L).get();
        log.info("{} {}", updated.getFirstName(), updated.getLastName());
    }
}

package com.example.kdtjpapractice.domain;

import com.example.kdtjpapractice.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void test() {
        //Given
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("youngji");
        customer.setLastName("jang");

        //When
        customerRepository.save(customer);

        //Then
        Customer entity = customerRepository.findById(1L).get();
        log.info("{} {}", entity.getFirstName(), entity.getLastName());
    }


}
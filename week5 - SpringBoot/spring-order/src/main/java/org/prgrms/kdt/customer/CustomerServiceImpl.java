package org.prgrms.kdt.customer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService {
    
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional(isolation = Isolation.DEFAULT) // 사용하는 DBMS의 default값을 사용하겠다.
    public void createCustomers(List<Customer> customers) {
        customers.forEach(customerRepository::insert);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer createCustomer(String email, String name) {
        // Customer 객체를 생성하것은 service에서
        return customerRepository.insert(new Customer(UUID.randomUUID(), name,email, LocalDateTime.now()));
    }

    @Override
    public Optional<Customer> getCustomer(UUID customerId) {
        return customerRepository.findById(customerId);
    }
}

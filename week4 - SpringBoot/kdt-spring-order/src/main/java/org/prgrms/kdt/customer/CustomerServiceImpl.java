package org.prgrms.kdt.customer;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {
    
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public void createCustomers(List<Customer> customers) {
        customers.forEach(customerRepository::insert);
    }
}

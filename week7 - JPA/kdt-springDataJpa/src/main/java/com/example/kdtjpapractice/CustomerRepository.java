package com.example.kdtjpapractice;

import com.example.kdtjpapractice.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}

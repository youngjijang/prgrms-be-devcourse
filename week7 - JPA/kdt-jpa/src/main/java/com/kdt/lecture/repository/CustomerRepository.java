package com.kdt.lecture.repository;

import com.kdt.lecture.repository.domain.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
}

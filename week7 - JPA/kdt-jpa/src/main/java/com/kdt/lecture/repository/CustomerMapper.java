package com.kdt.lecture.repository;

import com.kdt.lecture.repository.domain.Customer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper {

    void save(Customer customer);

    Customer findById(long id);
}

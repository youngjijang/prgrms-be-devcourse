package org.prgrms.kdt.customer;

import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerDTO(UUID customerId, String name, String email, LocalDateTime lastLoginAt,
                          LocalDateTime createdAt) { // DTO는 불변

    // dto로 mapping
    static CustomerDTO of(Customer customer) {
        return new CustomerDTO(customer.getCustomerId(),
                customer.getName(),
                customer.getEmail(),
                customer.getLastLoginAt(),
                customer.getCreatedAt());
    }

    // domain으로 mapping -> domain을 생성하는 것은 보통 서비스에서 만드는게 좀 더 낫다.
    static Customer to(CustomerDTO dto) {
        return new Customer(dto.customerId,
                dto.name,
                dto.email,
                dto.lastLoginAt,
                dto.createdAt);
    }
}

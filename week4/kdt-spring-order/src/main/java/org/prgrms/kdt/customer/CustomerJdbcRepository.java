package org.prgrms.kdt.customer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// DataSource 사용
@Repository
public class CustomerJdbcRepository implements CustomerRepository {

    private static final Logger logger = LoggerFactory.getLogger(CustomerJdbcRepository.class);

    private final JdbcTemplate jdbcTemplate;


    private static final RowMapper<Customer> customerRowMapper = new RowMapper<Customer>() {
        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException { //resultSet, index
            var customerName = rs.getString("name");
            var customerId = toUUID(rs.getBytes("customer_id"));
            var email = rs.getString("email");
            var createdAt = rs.getTimestamp("created_at").toLocalDateTime();
            var lastLoginAt = rs.getTimestamp("last_login_at") != null ?
                    rs.getTimestamp("last_login_at").toLocalDateTime() : null;

            return new Customer(customerId, customerName, email, lastLoginAt, createdAt);
        }
    };

    public CustomerJdbcRepository(JdbcTemplate jdbcTemplate, DataSource dataSource, DataSource dataSource1) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Customer insert(Customer customer) {
        var update = jdbcTemplate.update("INSERT INTO customers(customer_id,name,email, created_at)  VALUES (UUID_TO_BIN(?),?,?,?)",
                customer.getCustomerId().toString().getBytes() ,
                customer.getName(),
                customer.getEmail(),
                Timestamp.valueOf(customer.getCreatedAt()));
        if(update != 1) throw new RuntimeException("Nothing was inserted!");
        return customer;
    }

    @Override
    public Customer update(Customer customer) {
        var update = jdbcTemplate.update("UPDATE customers SET name= ?, email= ?, last_login_at= ? WHERE customer_id = UUID_TO_BIN(?)",
                customer.getName(),
                customer.getEmail(),
                customer.getLastLoginAt() != null ? Timestamp.valueOf(customer.getLastLoginAt()) : null,
                customer.getCustomerId().toString().getBytes());
        if(update != 1) throw new RuntimeException("Nothing was updated!");
        return customer;
    }

    @Override
    public int count() {
        return jdbcTemplate.queryForObject("select count(*) from customers",Integer.class);
    }


    @Override
    public List<Customer> findAll() {
        return jdbcTemplate.query("select * from customers", customerRowMapper); //sql,row mapper 전달
    }


    @Override
    public Optional<Customer> findById(UUID customerId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("select * from customers WHERE customer_id = UUID_TO_BIN(?)", customerRowMapper,customerId.toString().getBytes()));
            // queryForObject -> 하나만 꺼내기 값이 없으면 ㅇㅖ외발생
        }catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public Optional<Customer> findByName(String name) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("select * from customers WHERE name = ?", customerRowMapper,name));
            // queryForObject -> 하나만 꺼내기 값이 없으면 ㅇㅖ외발생
        }catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("select * from customers WHERE email = ?", customerRowMapper,email));
            // queryForObject -> 하나만 꺼내기 값이 없으면 ㅇㅖ외발생
        }catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM customers");
    }

    static UUID toUUID(byte[] bytes) {
        var byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }
}

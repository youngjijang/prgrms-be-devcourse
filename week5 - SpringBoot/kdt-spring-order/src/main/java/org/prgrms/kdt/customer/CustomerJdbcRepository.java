package org.prgrms.kdt.customer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

// DataSource 사용
@Repository
public class CustomerJdbcRepository implements CustomerRepository {

    private static final Logger logger = LoggerFactory.getLogger(CustomerJdbcRepository.class);


    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final TransactionTemplate transactionTemplate;


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

    private Map<String, Object> toParamMap(Customer customer) {
        return new HashMap<>(){{
            put("customerId", customer.getCustomerId().toString().getBytes());
            put("name",customer.getName());
            put("email",customer.getEmail());
            put("lastLoginAt", customer.getLastLoginAt() != null ? Timestamp.valueOf(customer.getLastLoginAt()):null);
            put("createdAt", customer.getCreatedAt());
        }};
    }


    public CustomerJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public Customer insert(Customer customer) {
        var update = jdbcTemplate.update("INSERT INTO customers(customer_id,name,email, created_at)" +
                "  VALUES (UUID_TO_BIN(:customerId),:name,:email,:createdAt)",toParamMap(customer));
        if(update != 1) throw new RuntimeException("Nothing was inserted!");
        return customer;
    }

    @Override
    public Customer update(Customer customer) {

        var update = jdbcTemplate.update("UPDATE customers SET name= :name, email= :email, last_login_at= :lastLoginAt  WHERE customer_id = UUID_TO_BIN(:customerId)",toParamMap(customer));
        if(update != 1) throw new RuntimeException("Nothing was updated!");
        return customer;
    }

    @Override
    public int count() {
        return jdbcTemplate.queryForObject("select count(*) from customers",Collections.EMPTY_MAP,Integer.class);
    }


    @Override
    public List<Customer> findAll() {
        return jdbcTemplate.query("select * from customers", customerRowMapper); //sql,row mapper 전달
    }


    @Override
    public Optional<Customer> findById(UUID customerId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("select * from customers WHERE customer_id = UUID_TO_BIN(:customerId)",
                    Collections.singletonMap("customerId",customerId.toString().getBytes()), customerRowMapper));
            // queryForObject -> 하나만 꺼내기 값이 없으면 ㅇㅖ외발생
        }catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public Optional<Customer> findByName(String name) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("select * from customers WHERE name = :name",Collections.singletonMap("name",name), customerRowMapper));
            // queryForObject -> 하나만 꺼내기 값이 없으면 ㅇㅖ외발생
        }catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("select * from customers WHERE email = :email",Collections.singletonMap("email",email) ,customerRowMapper));
            // queryForObject -> 하나만 꺼내기 값이 없으면 ㅇㅖ외발생
        }catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public void deleteAll() {
        //jdbcTemplate.update("DELETE FROM customers",Collections.emptyMap());

        jdbcTemplate.getJdbcTemplate().update("DELETE FROM customers");
        //empty map을 전달하기 귀찮으면 기존 템플릿을 가져와 사용해도된다.
    }

//    public void testTransaction(Customer customer){
//        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
//            @Override
//            protected void doInTransactionWithoutResult(TransactionStatus status) {
//                jdbcTemplate.update("UPDATE customers SET name= :name  WHERE customer_id = UUID_TO_BIN(:customerId)",toParamMap(customer));
//                jdbcTemplate.update("UPDATE customers SET email= :email WHERE customer_id = UUID_TO_BIN(:customerId)",toParamMap(customer));
//            }
//        });
//    }

    static UUID toUUID(byte[] bytes) {
        var byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }

}

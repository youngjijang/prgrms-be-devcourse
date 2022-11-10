package org.prgrms.kdt.customer;

import org.prgrms.kdt.JdbcCustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.nio.ByteBuffer;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// DataSource 사용
@Repository
public class CustomerJdbcRepository implements CustomerRepository {

    private static final Logger logger = LoggerFactory.getLogger(JdbcCustomerRepository.class);

    private final DataSource dataSource;

    public CustomerJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Customer insert(Customer customer) {
        try (
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("INSERT INTO customers(customer_id,name,email, created_at)  VALUES (UUID_TO_BIN(?),?,?,?)");
        ) {
            statement.setBytes(1, customer.getCustomerId().toString().getBytes());
            statement.setString(2, customer.getName());
            statement.setString(3, customer.getEmail());
            statement.setTimestamp(4, Timestamp.valueOf(customer.getCreatedAt()));
            var executeUpdate = statement.executeUpdate();
            if (executeUpdate != 1) throw new RuntimeException("Nothing was inserted!");
            return customer;
        } catch (SQLException e) {
            logger.error("Got error while closing connection", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Customer update(Customer customer) {
        try(
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("UPDATE customers SET name= ?, email= ?, last_login_at= ? WHERE customer_id = UUID_TO_BIN(?)");
        ) {
            statement.setString(1,customer.getName());
            statement.setString(2,customer.getEmail());
            statement.setTimestamp(3,customer.getLastLoginAt() != null ? Timestamp.valueOf(customer.getLastLoginAt()) : null);
            statement.setBytes(4,customer.getCustomerId().toString().getBytes());
            var executeUpdate = statement.executeUpdate();
            if (executeUpdate != 1) throw new RuntimeException("Nothing was updated!");
            return customer;
        }
        catch (SQLException e) {
            logger.error("Got error while closing connection", e);
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        try (
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("select * from customers");
                var resultSet = statement.executeQuery();
        ) {
            while (resultSet.next()) {
                mapToCustomer(customers, resultSet);
            }
        } catch (SQLException e) {
            logger.error("Got error while closing connection", e);
            throw new RuntimeException(e);
        }
        return customers;
    }


    @Override
    public Optional<Customer> findById(UUID customerId) {
        List<Customer> customers = new ArrayList<>();
        try (
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("select * from customers WHERE customer_id = UUID_TO_BIN(?)");
        ) {
            statement.setBytes(1, customerId.toString().getBytes()); // 인텍스는 1
            try (var resultSet = statement.executeQuery();) {
                while (resultSet.next()) {
                    mapToCustomer(customers, resultSet);
                }

            }
        } catch (SQLException e) {
            logger.error("Got error while closing connection", e);
            throw new RuntimeException(e);
        }
        return customers.stream().findFirst();
    }

    @Override
    public Optional<Customer> findByName(String name) {
        List<Customer> customers = new ArrayList<>();
        try (
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("select * from customers WHERE name = ?");
        ) {
            statement.setString(1, name); // 인텍스는 1부
            try (var resultSet = statement.executeQuery();) {
                while (resultSet.next()) {
                    mapToCustomer(customers, resultSet);
                }
            }
        } catch (SQLException e) {
            logger.error("Got error while closing connection", e);
            throw new RuntimeException(e);
        }
        return customers.stream().findFirst();
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        List<Customer> customers = new ArrayList<>();
        try (
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("select * from customers WHERE email = ?");
        ) {
            statement.setString(1, email); // 인텍스는 1
            try (var resultSet = statement.executeQuery();) {
                while (resultSet.next()) {
                    mapToCustomer(customers, resultSet);
                }
            }
        } catch (SQLException e) {
            logger.error("Got error while closing connection", e);
            throw new RuntimeException(e);
        }
        return customers.stream().findFirst();
    }

    @Override
    public void deleteAll() {
        try (
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("DELETE FROM customers");
        ) {
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Got error while closing connection", e);
        }
    }

    private static void mapToCustomer(List<Customer> customers, ResultSet resultSet) throws SQLException {
        var customerName = resultSet.getString("name");
        var customerId = toUUID(resultSet.getBytes("customer_id"));
        var email = resultSet.getString("email");
        var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
        var lastLoginAt = resultSet.getTimestamp("last_login_at") != null ?
                resultSet.getTimestamp("last_login_at").toLocalDateTime() : null;

        logger.info("customer id =>{}, customer name =>{}, createdAt => {}", customerId, customerName, createdAt);
        customers.add(new Customer(customerId, customerName, email, lastLoginAt, createdAt));
    }

    static UUID toUUID(byte[] bytes) {
        var byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }
}

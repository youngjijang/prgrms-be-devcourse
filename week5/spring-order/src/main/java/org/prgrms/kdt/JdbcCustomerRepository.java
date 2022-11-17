package org.prgrms.kdt;

import org.prgrms.kdt.customer.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


// DriverManager 사용
public class JdbcCustomerRepository {
    private static final Logger logger = LoggerFactory.getLogger(JdbcCustomerRepository.class);
    private final String SELECT_ALL_SQL = "select * from customers";
    private final String SELECT_BY_NAME_SQL = "select * from customers WHERE name = ?";
    private final String INSERT_SQL = "INSERT INTO customers(customer_id,name,email)  VALUES (UUID_TO_BIN(?),?,?)";
    private final String UPDATE_BY_ID_SQL = "UPDATE customers SET name= ? WHERE customer_id = UUID_TO_BIN(?)";
    private final String DELETE_ALL_SQL = "DELETE FROM customers";


    public List<String> findALLNames(){
        List<String> names = new ArrayList<>();
        try(
            var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt","root","dudwl0804!");
            var statement = connection.prepareStatement(SELECT_ALL_SQL);
            var resultSet = statement.executeQuery();
        ) {
            while(resultSet.next()){
                var customerName = resultSet.getString("name");
                var customerId =  toUUID(resultSet.getBytes("customer_id"));
                var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
                logger.info("customer id =>{}, customer name =>{}, createdAt ={}",customerId,customerName,createdAt);
                names.add(customerName);

            }
        } catch (SQLException e) {
            logger.error("Got error while closing connection", e);
        }
        return names;
    }


    public List<String> findByNames(String name){

        List<String> names = new ArrayList<>();
        try(
            var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt","root","dudwl0804!");
            var statement = connection.prepareStatement(SELECT_BY_NAME_SQL);
        ) {
            statement.setString(1,name); // 인텍스는 1부
            logger.info("statement =>{}",statement);
            try(var resultSet = statement.executeQuery();){
                while(resultSet.next()){
                    var customerName = resultSet.getString("name");
                    var customerId =  toUUID(resultSet.getBytes("customer_id"));
                    var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
                    logger.info("customer id =>{}, customer name =>{}, createdAt ={}",customerId,name,createdAt);
                    names.add(customerName);
                }
            }
        } catch (SQLException e) {
            logger.error("Got error while closing connection", e);
        }
        return names;
    }

    public int insertCustomer(UUID customerId, String name, String email){
        try(
            var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt","root","dudwl0804!");
            var statement = connection.prepareStatement(INSERT_SQL);
        ) {
            statement.setBytes(1,customerId.toString().getBytes());
            statement.setString(2,name);
            statement.setString(3,email);
            return statement.executeUpdate();
        }
        catch (SQLException e) {
            logger.error("Got error while closing connection", e);
        }
        return 0;
    }

    public int updateCustomerName(String name, UUID customerId){
        try(
            var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt","root","dudwl0804!");
            var statement = connection.prepareStatement(UPDATE_BY_ID_SQL);
        ) {
            statement.setString(1,name);
            statement.setBytes(2,customerId.toString().getBytes());
            logger.info("customer id =>{}, customer name =>{}",customerId,name);
            return statement.executeUpdate();
        }
        catch (SQLException e) {
            logger.error("Got error while closing connection", e);
        }
        return 0;
    }

    public int deleteAllCustomer(){
        try(
            var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt","root","dudwl0804!");
            var statement = connection.prepareStatement(DELETE_ALL_SQL);
        ) {
            return statement.executeUpdate();
        }
        catch (SQLException e) {
            logger.error("Got error while closing connection", e);
        }
        return 0;
    }

    static UUID toUUID(byte[] bytes){
        var byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(),byteBuffer.getLong());
    }

    public void transactionTest(Customer customer){
        String updateNameSQL = "UPDATE customers SET name= ? WHERE customer_id = UUID_TO_BIN(?)";
        String updateUpdateSQL = "UPDATE customers SET email= ? WHERE customer_id = UUID_TO_BIN(?)";

        // 미완성
        try(var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt","root","dudwl0804!");
            var nameStatement = connection.prepareStatement(updateNameSQL);
            var emailStatement = connection.prepareStatement(updateUpdateSQL);
            ){
            // connection.setAutoCommit(false);
            nameStatement.setString(1,customer.getName());
            nameStatement.setBytes(2,customer.getCustomerId().toString().getBytes());
            nameStatement.executeUpdate();

            emailStatement.setString(1,customer.getEmail());
            emailStatement.setBytes(2,customer.getCustomerId().toString().getBytes());
            emailStatement.executeUpdate();
            // connection.setAutoCommit(true);
        }catch (SQLException e){
            logger.error("Got error while closing connection", e);
        }
    }

    public static void main(String[] args) throws SQLException {
        var customerRepository = new JdbcCustomerRepository();

        var count = customerRepository.deleteAllCustomer();
        logger.info("delete count => {}",count);
        customerRepository.insertCustomer(UUID.randomUUID(), "new-user","new-user@gmail.com");
        var customer2 = UUID.randomUUID();
        customerRepository.insertCustomer(customer2, "new-user2","new-user2@gmail.com");
        customerRepository.updateCustomerName("update-user",customer2);

        var names = customerRepository.findALLNames();
        System.out.println(names);
    }
}

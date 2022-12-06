package com.example.gccoffee.repository;

import com.example.gccoffee.model.*;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

import static com.example.gccoffee.JdbcUtils.toLocalDateTime;
import static com.example.gccoffee.JdbcUtils.toUUID;

@Repository
public class OrderJdbcRepository implements OrderRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;


  public OrderJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  @Transactional
  public Order insert(Order order) {
    jdbcTemplate.update("INSERT INTO orders(order_id, email, address, postcode, order_status, create_at, update_at) " +
        "VALUES (UUID_TO_BIN(:orderId), :email, :address, :postcode, :orderStatus, :createAt, :updateAt)",
      toOrderParamMap(order));
    order.getOrderItems()
      .forEach(item ->
        jdbcTemplate.update("INSERT INTO order_item(order_id, product_id, category, price, quantity, create_at, update_at) " +
            "VALUES (UUID_TO_BIN(:orderId), UUID_TO_BIN(:productId), :category, :price, :quantity, :createAt, :updateAt)",
          toOrderItemParamMap(order.getOrderId(), order.getCreatedAt(), order.getUpdateAt(), item)));
    return order;
  }


  @Override
  @Transactional
  public Optional<Order> findById(UUID orderId) {
    try {
      var order = jdbcTemplate.queryForObject("select * from orders WHERE order_id = UUID_TO_BIN(:orderId)", Collections.singletonMap("orderId", orderId.toString().getBytes()),orderRowMapper);
      var orderItems = jdbcTemplate.query("select * from order_item WHERE order_id = :orderId",
              Collections.singletonMap("orderId",orderId.toString().getBytes()),orderItemRowMapper);
      orderItems.forEach(orderItem -> order.getOrderItems().add(orderItem));
      return Optional.ofNullable(order);
    }catch (EmptyResultDataAccessException e){
      return Optional.empty();
    }
  }

  private Map<String, Object> toOrderParamMap(Order order) {
    var paramMap = new HashMap<String, Object>();
    paramMap.put("orderId", order.getOrderId().toString().getBytes());
    paramMap.put("email", order.getEmail().getAddress());
    paramMap.put("address", order.getAddress());
    paramMap.put("postcode", order.getPostcode());
    paramMap.put("orderStatus", order.getOrderStatus().toString());
    paramMap.put("createAt", order.getCreatedAt());
    paramMap.put("updateAt", order.getUpdateAt());
    return paramMap;
  }

  private Map<String, Object> toOrderItemParamMap(UUID orderId, LocalDateTime createdAt, LocalDateTime updatedAt, OrderItem item) {
    var paramMap = new HashMap<String, Object>();
    paramMap.put("orderId", orderId.toString().getBytes());
    paramMap.put("productId", item.productId().toString().getBytes());
    paramMap.put("category", item.category().toString());
    paramMap.put("price", item.price());
    paramMap.put("quantity", item.quantity());
    paramMap.put("createAt", createdAt);
    paramMap.put("updateAt", updatedAt);
    return paramMap;
  }

  private static final RowMapper<OrderItem> orderItemRowMapper = (resultSet, i) ->{
    var productId = toUUID(resultSet.getBytes("product_id"));
    var category = Category.valueOf(resultSet.getString("category"));
    var price = resultSet.getLong("price");
    var quantity = resultSet.getInt("description");
    return new OrderItem(productId,  category, price, quantity);
  };;

  private static final RowMapper<Order> orderRowMapper =(resultSet, i) -> {
    var orderId = toUUID(resultSet.getBytes("order_id"));
    var email = new Email(resultSet.getString("email"));
    var address = resultSet.getString("address");
    var postcode = resultSet.getString("postcode");
    var orderStatus = OrderStatus.valueOf(resultSet.getString("order_status"));
    var createAt = toLocalDateTime(resultSet.getTimestamp("create_at"));
    var updateAt = toLocalDateTime(resultSet.getTimestamp("update_at"));
    return new Order(orderId, email, address, postcode, new ArrayList<>(),orderStatus, createAt, updateAt);
  };

}

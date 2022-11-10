package org.prgrms.kdt.customer;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.sql.DataSource;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // PER_CLASS : class 마다 인스턴스가 하나
class CustomerJdbcRepositoryTest {

    @Configuration
    @ComponentScan(
            basePackages = {"org.prgrms.kdt.customer"}
    )
    static class Config {

        @Bean
        public DataSource dataSource() {
            HikariDataSource dataSource = DataSourceBuilder.create()
                    .url("jdbc:mysql://localhost/order_mgmt")
                    .username("root")
                    .password("dudwl0804!")
                    .type(HikariDataSource.class) // datasource 만들 구현체 타입 지정
                    .build();
            // connection pool은 기본적으로 10개의 connection을 만든다.
            // 100개로 늘리고 싶다면
            //dataSource.setMaximumPoolSize(1000);
            //dataSource.setMinimumIdle(100);
            return dataSource;
        }

        @Bean
        public JdbcTemplate jdbcTemplate (DataSource dataSource){
            return new JdbcTemplate(dataSource);
        }
    }

    @Autowired
    CustomerJdbcRepository customerJdbcRepository;

    @Autowired
    DataSource dataSource;

    Customer newCustomers; // 전역변수로 등록하여 하드코딩하지 않고 getter를 이용한다.

    @BeforeAll
    void setup(){
        customerJdbcRepository.deleteAll();
        newCustomers = new Customer(UUID.randomUUID(), "test-user", "test_user@gmail.com", LocalDateTime.now());
    }

    @Test
    @Order(1)
    @DisplayName("Hikari 연결 확인")
    public void testHikariConnectionPool() {
        assertThat(dataSource.getClass().getName(), is("com.zaxxer.hikari.HikariDataSource"));
    }

    @Test
    @Order(2)
    @DisplayName("고객을 추가할 수 있다.")
    public void testInsert() {

        customerJdbcRepository.insert(newCustomers);

        var retrievedCustomer = customerJdbcRepository.findById(newCustomers.getCustomerId());
        assertThat(retrievedCustomer.isEmpty(), is(false));
        assertThat(retrievedCustomer.get(), samePropertyValuesAs(newCustomers));
        // samePropertyValuesAs -> 안에 있는 property들을 다 검사해준다.
    }


    @Test
    @Order(3)
    @DisplayName("전체 고객을 조회할 수 있다.")
    public void testFindAll() {
        var customers = customerJdbcRepository.findAll();
        assertThat(customers.isEmpty(), is(false));
    }

    @Test
    @Order(4)
    @DisplayName("이름으로 고객을 조회할 수 있다.")
    public void testFindByName() {
        var customers = customerJdbcRepository.findByName(newCustomers.getName());
        assertThat(customers.isEmpty(), is(false));

        var unknown = customerJdbcRepository.findByName("unknown");
        assertThat(unknown.isEmpty(), is(true));
    }

    @Test
    @Order(5)
    @DisplayName("이메일로 고객을 조회할 수 있다.")
    public void testFindByEmail() {
        var customers = customerJdbcRepository.findByEmail(newCustomers.getEmail());
        assertThat(customers.isEmpty(), is(false));

        var unknown = customerJdbcRepository.findByEmail("unknown");
        assertThat(unknown.isEmpty(), is(true));
    }

    @Test
    @Order(6)
    @DisplayName("고객 정보를 수정할 수 있다.")
    public void testUpdateByEmail() {
        newCustomers.changeName("update-user");
        customerJdbcRepository.update(newCustomers);

        var all = customerJdbcRepository.findAll();
        assertThat(all,hasSize(1));
        assertThat(all, everyItem(samePropertyValuesAs(newCustomers)));

        var retrievedCustomer = customerJdbcRepository.findById(newCustomers.getCustomerId());
        assertThat(retrievedCustomer.isEmpty(),is(false));
        assertThat(retrievedCustomer.get(),samePropertyValuesAs(newCustomers) );
    }
}
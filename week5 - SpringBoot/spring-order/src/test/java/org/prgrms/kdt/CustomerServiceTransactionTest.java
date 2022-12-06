package org.prgrms.kdt;


import com.wix.mysql.EmbeddedMysql;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.prgrms.kdt.customer.Customer;
import org.prgrms.kdt.customer.CustomerJdbcRepository;
import org.prgrms.kdt.customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.ScriptResolver.classPathScript;
import static com.wix.mysql.config.Charset.UTF8;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.distribution.Version.v8_0_11;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@SpringJUnitConfig
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // PER_CLASS : class 마다 인스턴스가 하나
class CustomerServiceTransactionTest {

    @Configuration
    @ComponentScan(
            basePackages = {"org.prgrms.kdt.customer"}
    )
    @EnableTransactionManagement
    static class Config {

        @Bean
        public DataSource dataSource() {

            HikariDataSource dataSource = DataSourceBuilder.create()
                    .url("jdbc:mysql://localhost:2215/test-order_mgmt")
                    .username("test")
                    .password("test1234!")
                    .type(HikariDataSource.class) // datasource 만들 구현체 타입 지정
                    .build();
            return dataSource;
        }

        @Bean
        public NamedParameterJdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new NamedParameterJdbcTemplate(dataSource);
        }

        @Bean
        public PlatformTransactionManager platformTransactionManager(DataSource dataSource){
            return new DataSourceTransactionManager(dataSource);
        }

        @Bean
        public TransactionTemplate transactionTemplate(PlatformTransactionManager platformTransactionManager){
            return new TransactionTemplate(platformTransactionManager);
        }
    }

    @Autowired
    CustomerJdbcRepository customerJdbcRepository;

    @Autowired
    CustomerService customerService;

    Customer newCustomers; // 전역변수로 등록하여 하드코딩하지 않고 getter를 이용한다.

    private static EmbeddedMysql embeddedMysql;

    @BeforeAll
    void setup() {
        //customerJdbcRepository.deleteAll(); // embedded 사용시 어차피 싹다 디비가 내려갔다 올라오기 때문에 삭제해줄 필요없음
        var mysqlConfig = aMysqldConfig(v8_0_11)
                .withCharset(UTF8)
                .withPort(2215)
                .withUser("test", "test1234!")
                .withTimeZone("Asia/Seoul")
                .build();
        embeddedMysql = anEmbeddedMysql(mysqlConfig)
                .addSchema("test-order_mgmt", classPathScript("schema.sql"))
                .start();

        newCustomers = new Customer(UUID.randomUUID(), "test-user", "test_user@gmail.com", LocalDateTime.now());
    }

    @AfterEach
    void dataCleanup(){
        customerJdbcRepository.deleteAll();
    }

    @AfterAll
    void cleanUp() {
        embeddedMysql.stop();
    }

    @Test
    @DisplayName("다건 추가 테스트")
    void multiInsertTest(){
        var customers = List.of(
                new Customer(UUID.randomUUID(), "a", "a@a.com", LocalDateTime.now()),
                new Customer(UUID.randomUUID(), "b", "b@b.com", LocalDateTime.now())
        );

        customerService.createCustomers(customers);
        var allCustomers = customerJdbcRepository.findAll();
        assertThat(allCustomers.size(),is(2));
        assertThat(allCustomers, containsInAnyOrder(samePropertyValuesAs(customers.get(0)),samePropertyValuesAs(customers.get(1))));
    }


    @Test
    @DisplayName("다건 추가 실패시 롤백 테스트")
    void multiInsertRollbackTest(){
        var customers = List.of(
                new Customer(UUID.randomUUID(), "a", "a@a.com", LocalDateTime.now()),
                new Customer(UUID.randomUUID(), "b", "a@a.com", LocalDateTime.now())
        );

        try {
            customerService.createCustomers(customers);
        }catch (DataAccessException e){

        }

        var allCustomers = customerJdbcRepository.findAll();
        assertThat(allCustomers.isEmpty(),is(true));
        assertThat(allCustomers, not( containsInAnyOrder(samePropertyValuesAs(customers.get(0)),samePropertyValuesAs(customers.get(1)))));
    }

}
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.ScriptResolver.classPathScript;
import static com.wix.mysql.config.Charset.UTF8;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.distribution.Version.v8_0_11;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@SpringJUnitConfig
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // PER_CLASS : class 마다 인스턴스가 하나
class CustomerServiceTest {

    private static EmbeddedMysql embeddedMysql;
    @Configuration
    @ComponentScan
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
        public JdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }

    }

    @Autowired
    CustomerJdbcRepository customerJdbcRepository;

    @Autowired
    CustomerService customerService;

    Customer newCustomers; // 전역변수로 등록하여 하드코딩하지 않고 getter를 이용한다.


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

    @AfterAll
    void cleanUp() {
        embeddedMysql.stop();
    }



}
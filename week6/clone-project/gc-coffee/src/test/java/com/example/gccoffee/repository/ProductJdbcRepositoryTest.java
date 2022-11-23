package com.example.gccoffee.repository;

import com.example.gccoffee.model.Category;
import com.example.gccoffee.model.Product;
import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.ScriptResolver;
import com.wix.mysql.config.Charset;
import com.wix.mysql.distribution.Version;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.config.MysqldConfig.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
class ProductJdbcRepositoryTest {

    static EmbeddedMysql embeddedMysql;


    @BeforeAll
    static void setup(){
        var config = aMysqldConfig(Version.v8_0_11)
                .withCharset(Charset.UTF8)
                .withPort(2215)
                .withUser("test","test1234!")
                .withTimeZone("Asia/Seoul")
                .build();
        embeddedMysql = anEmbeddedMysql(config)
                .addSchema("test-order_mgmt", ScriptResolver.classPathScript("schema.sql"))
                .start();
    }

    @AfterAll
    static void cleanup(){
        embeddedMysql.stop();
    }

    @Autowired
    ProductRepository productRepository;

    private final static Product newProduct = new Product(UUID.randomUUID(), "new-product", Category.COFFEE_BEEN_PACKAGE, 1000L);

    @Test
    @Order(1)
    @DisplayName("상품을 추가할 수 있다.")
    void testInsert(){
        productRepository.insert(newProduct);
        var all = productRepository.findAll();
        assertThat(all.isEmpty(), is(false));
    }

    @Test
    @Order(2)
    @DisplayName("상품을 이름으로 조회할 수 있다.")
    void testByName(){
        var product = productRepository.findByName(newProduct.getProductName());
        assertThat(product.isEmpty(), is(false));
    }

    @Test
    @Order(3)
    @DisplayName("상품을 카테고리로 조회할 수 있다.")
    void testByCategory(){
        var product = productRepository.findByCategory(newProduct.getCategory());
        assertThat(product.isEmpty(), is(false));
    }

    @Test
    @Order(4)
    @DisplayName("상품을 id로 조회할 수 있다.")
    void testById(){
        var product = productRepository.findById(newProduct.getProductId());
        assertThat(product.isEmpty(), is(false));
    }


    @Test
    @Order(5)
    @DisplayName("상품을 업데이트할 수 있다.")
    void testUpdate(){
        newProduct.setProductName("update-product");
        productRepository.update(newProduct);

        var product = productRepository.findById(newProduct.getProductId());
        assertThat(product.isEmpty(), is(false));
        assertThat(product.get(), samePropertyValuesAs(newProduct));
    }


    @Test
    @Order(6)
    @DisplayName("상품을 전체 삭제할 수 있다.")
    void testDeleteAll(){
        productRepository.deleteAll();
        var all = productRepository.findAll();
        assertThat(all.isEmpty(), is(true));
    }


}
package org.prgrms.kdt.servlet;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.EncodedResourceResolver;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

public class KdtWebApplicationInitializer implements WebApplicationInitializer {

    private static final Logger logger = LoggerFactory.getLogger(KdtWebApplicationInitializer.class);

    @EnableWebMvc // spring api에 필요한 bean들이 자동으로 등록
    @Configuration
    @ComponentScan
    @EnableTransactionManagement
    static class AppConfig implements WebMvcConfigurer {


        @Override
        public void configureViewResolvers(ViewResolverRegistry registry) {
            registry.jsp();
        }

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/resources/**")
                    .addResourceLocations("/resources")
                    .setCachePeriod(60) // 캐시보관 시간
                    .resourceChain(true)
                    .addResolver(new EncodedResourceResolver()); // 알아서 압축해줌
        }

        @Bean
        public DataSource dataSource() {

            HikariDataSource dataSource = DataSourceBuilder.create()
                    .url("jdbc:mysql://localhost:2215/order_mgmt")
                    .username("root")
                    .password("dudwl1234!")
                    .type(HikariDataSource.class) // datasource 만들 구현체 타입 지정
                    .build();
            return dataSource;
        }

        @Bean
        public NamedParameterJdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new NamedParameterJdbcTemplate(dataSource);
        }

        @Bean
        public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }

        @Bean
        public TransactionTemplate transactionTemplate(PlatformTransactionManager platformTransactionManager){
            return new TransactionTemplate(platformTransactionManager);
        }

    }

    @Override
    public void onStartup(ServletContext servletContext) {
        var applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(AppConfig.class);

        var dispatcherServlet = new DispatcherServlet(applicationContext);

        logger.info("starting server...");
        var servletRegistration = servletContext.addServlet("test", dispatcherServlet);
        servletRegistration.addMapping("/");
    }
}

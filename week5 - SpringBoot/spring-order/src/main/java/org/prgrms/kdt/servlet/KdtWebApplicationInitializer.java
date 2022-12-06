package org.prgrms.kdt.servlet;

import com.zaxxer.hikari.HikariDataSource;
import org.prgrms.kdt.customer.CustomerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
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
    @ComponentScan(basePackages = "org.prgrms.kdt.customer",
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = CustomerController.class),
        useDefaultFilters = false)// root가 아니니까 controller만 scan
    static class ServletConfig implements WebMvcConfigurer, ApplicationContextAware {

        ApplicationContext applicationContext;
        @Override
        public void configureViewResolvers(ViewResolverRegistry registry) {
            //registry.jsp();

            var springResourceTemplateResolver = new SpringResourceTemplateResolver();
            springResourceTemplateResolver.setApplicationContext(applicationContext);
            springResourceTemplateResolver.setPrefix("/WEB-INF/");
            springResourceTemplateResolver.setSuffix(".html");
            var springTemplateEngine = new SpringTemplateEngine();
            springTemplateEngine.setTemplateResolver(springResourceTemplateResolver);

            var thymeleafViewResolver = new ThymeleafViewResolver();
            thymeleafViewResolver.setTemplateEngine(springTemplateEngine);
            thymeleafViewResolver.setOrder(1);
            thymeleafViewResolver.setViewNames(new String[]{"views/*"});
            registry.viewResolver(thymeleafViewResolver);
        }

        // 정적 리소스
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/resources/**")
                    .addResourceLocations("/resources/")
                    .setCachePeriod(60) // 캐시보관 시간
                    .resourceChain(true)
                    .addResolver(new EncodedResourceResolver()); // 알아서 압축해줌
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }
    }

    @Configuration
    @EnableTransactionManagement
    @ComponentScan(basePackages = "org.prgrms.kdt.customer",
                  excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = CustomerController.class))// root가 아니니까 controller만 scan
    static class RootConfig{
        @Bean
        public DataSource dataSource() {
            HikariDataSource dataSource = DataSourceBuilder.create()
                    .url("jdbc:mysql://localhost/order_mgmt")
                    .username("root")
                    .password("dudwl0804!")
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

    @Override
    public void onStartup(ServletContext servletContext) {
        logger.info("starting server...");
        //rootApplicationContext을 만들기 위해 ContextLoaderListener 추가하기
        var rootApplicationContext = new AnnotationConfigWebApplicationContext();
        rootApplicationContext.register(RootConfig.class);
        var loaderListener = new ContextLoaderListener(rootApplicationContext);
        servletContext.addListener(loaderListener);

        // --------------------------------------------------------

        // servlet은 여러개를 등록할 수 있다.
        var applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(ServletConfig.class);

        var dispatcherServlet = new DispatcherServlet(applicationContext);
        var servletRegistration = servletContext.addServlet("test", dispatcherServlet);
        servletRegistration.addMapping("/");
        servletRegistration.setLoadOnStartup(0);
        // default -1 : load가 안되다가 api 호출이 오면 그때 load됨
    }
}

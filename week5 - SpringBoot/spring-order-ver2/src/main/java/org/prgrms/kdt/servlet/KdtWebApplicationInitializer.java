package org.prgrms.kdt.servlet;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.zaxxer.hikari.HikariDataSource;
import org.prgrms.kdt.customer.CustomerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.resource.EncodedResourceResolver;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;


import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class KdtWebApplicationInitializer implements WebApplicationInitializer {

    private static final Logger logger = LoggerFactory.getLogger(KdtWebApplicationInitializer.class);

    @EnableWebMvc // spring api??? ????????? bean?????? ???????????? ??????
    @Configuration
    @ComponentScan(basePackages = "org.prgrms.kdt.customer",
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = CustomerController.class),
        useDefaultFilters = false)// root??? ???????????? controller??? scan
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

        // ?????? ?????????
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/resources/**")
                    .addResourceLocations("/resources/")
                    .setCachePeriod(60) // ???????????? ??????
                    .resourceChain(true)
                    .addResolver(new EncodedResourceResolver()); // ????????? ????????????
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }

        @Override
        public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

            // xml messageConverter
            var messageConverter = new MarshallingHttpMessageConverter();
            var xStreamMarshaller = new XStreamMarshaller();
            messageConverter.setMarshaller(xStreamMarshaller);
            messageConverter.setUnmarshaller(xStreamMarshaller);
            converters.add(0,messageConverter);

            // json ?????? ?????? : LocalTime ???????????? ??????
            var javaTimeModule = new JavaTimeModule();
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE));
            //java object??? json?????? ????????? ?????? ?????? mapper Jackson2ObjectMapperBuilder
            var module = Jackson2ObjectMapperBuilder.json().modules(javaTimeModule);
            converters.add(1, new MappingJackson2HttpMessageConverter(module.build()));
        }

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/api/**")
                    .allowedMethods("GET","POST")
                    .allowedOrigins("*");
        }
    }

    @Configuration
    @EnableTransactionManagement
    @ComponentScan(basePackages = "org.prgrms.kdt.customer",
                  excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = CustomerController.class))// root??? ???????????? controller??? scan
    static class RootConfig{
        @Bean
        public DataSource dataSource() {
            HikariDataSource dataSource = DataSourceBuilder.create()
                    .url("jdbc:mysql://localhost/order_mgmt")
                    .username("root")
                    .password("dudwl0804!")
                    .type(HikariDataSource.class) // datasource ?????? ????????? ?????? ??????
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
        //rootApplicationContext??? ????????? ?????? ContextLoaderListener ????????????
        var rootApplicationContext = new AnnotationConfigWebApplicationContext();
        rootApplicationContext.register(RootConfig.class);
        var loaderListener = new ContextLoaderListener(rootApplicationContext);
        servletContext.addListener(loaderListener);

        // --------------------------------------------------------

        // servlet??? ???????????? ????????? ??? ??????.
        var applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(ServletConfig.class);

        var dispatcherServlet = new DispatcherServlet(applicationContext);
        var servletRegistration = servletContext.addServlet("test", dispatcherServlet);
        servletRegistration.addMapping("/");
        servletRegistration.setLoadOnStartup(0);
        // default -1 : load??? ???????????? api ????????? ?????? ?????? load???
    }
}

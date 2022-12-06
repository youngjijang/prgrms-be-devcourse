package org.prgrms.kdt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedMethods("GET","POST")
                .allowedOrigins("*");
    }

    // xml 추가 - javaTimeModule은 Springboot가 알아서 해줘서 추가해줄 필요없다.
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // xml messageConverter
        var messageConverter = new MarshallingHttpMessageConverter();
        var xStreamMarshaller = new XStreamMarshaller();
        messageConverter.setMarshaller(xStreamMarshaller);
        messageConverter.setUnmarshaller(xStreamMarshaller);
        converters.add(0,messageConverter);
    }
}

package com.prgrms.config;

import net.sf.log4jdbc.Log4jdbcProxyDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DataSourcePostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 파라미터로 전달되는 bean이 순수 datasource 타입인지 확인하여,Log4jdbcProxyDataSource로 감싸준다.
        // connect에 로깅 처리를 할수 있게 가공
        if(bean instanceof DataSource && !(bean instanceof Log4jdbcProxyDataSource)){
            return new Log4jdbcProxyDataSource((DataSource) bean);
        }
        return bean;
    }


    // 순환참조로 인한 임시방편이동,,, 다른 곳으로 빼줘야한다.ㅜ
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

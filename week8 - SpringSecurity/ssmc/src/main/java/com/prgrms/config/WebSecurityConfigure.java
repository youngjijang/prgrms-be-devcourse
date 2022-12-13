package com.prgrms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfigure extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
            .withUser("user").password("{noop}user123").roles("USER")
            .and()
            .withUser("admin").password("{noop}admin123").roles("ADMIN");
  }

  @Override
  public void configure(WebSecurity web) {
    web.ignoring().antMatchers("/assets/**");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
          .authorizeRequests()
            .antMatchers("/me").hasAnyRole("USER", "ADMIN")
            .anyRequest().permitAll()
            .and()
          .formLogin()
            .defaultSuccessUrl("/")
            .permitAll()
            .and()
          /**
           * remember me 설정
           */
          .rememberMe()
            .rememberMeParameter("remember-me")
            .tokenValiditySeconds(300)
            .and()
          /**
           * 로그아웃 설정
           */
          .logout()
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // 헤딩 matcher로 들어오면 logout
            .logoutSuccessUrl("/")
            .invalidateHttpSession(true)
            .clearAuthentication(true)
            .and()
            /**
             * HTTP 요청을 HTTPS 요청으로 리다이렉트
             */
          .requiresChannel()
          .anyRequest().requiresSecure() //모든 요청은 requiresSecure channel(https) 로 전달한다.
    ;
  }

}
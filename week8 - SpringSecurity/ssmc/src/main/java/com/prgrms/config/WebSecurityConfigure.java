package com.prgrms.config;

import org.slf4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
            .antMatchers("/admin").access("hasRole('ADMIN') and isFullyAuthenticated()") // isFullyAuthenticated -> remember me에서 인증ㅇ된 사용자는 안된다.
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
            .key("my-remember-me")
            .rememberMeParameter("remember-me")
            .tokenValiditySeconds(300) // 만료시간
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
            .and()
            /**
             * anonymous 사용자 설정
             */
          .anonymous()
            .principal("thisIsAnonymousUser")
            .authorities("ROLE_ANONYMOUS", "ROLE_UNKNOWN")
            .and()
          .exceptionHandling()
            .accessDeniedHandler(accessDeniedHandler())

    ;
  }

  public AccessDeniedHandler accessDeniedHandler(){
    return new AccessDeniedHandler() {
      @Override
      public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // 사용자 정보 가져오기
        Object principal = authentication != null ? authentication.getPrincipal() : null;
        httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN); //404?
        httpServletResponse.setContentType("text/plain");
        httpServletResponse.getWriter().write("### ACCESS DENIED ###");
        httpServletResponse.getWriter().flush();
        httpServletResponse.getWriter().close();
      }
    };
  }

}
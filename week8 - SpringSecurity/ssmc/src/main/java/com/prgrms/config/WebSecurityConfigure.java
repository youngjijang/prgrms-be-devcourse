package com.prgrms.config;

import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfigure extends WebSecurityConfigurerAdapter {

  @Bean
  public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(3);
    executor.setMaxPoolSize(5);
    executor.setThreadNamePrefix("my-executor-");
    return executor;
  }

  @Bean
  public DelegatingSecurityContextAsyncTaskExecutor taskExecutor(ThreadPoolTaskExecutor delegate) {
    return new DelegatingSecurityContextAsyncTaskExecutor(delegate);
  }


//  public WebSecurityConfigure() {
//    SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
//  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
            .withUser("user").password("{noop}user123").roles("USER")
            .and()
            .withUser("admin01").password("{noop}admin123").roles("ADMIN")
            .and()
            .withUser("admin02").password("{noop}admin123").roles("ADMIN");
  }

  @Override
  public void configure(WebSecurity web) {
    web.ignoring().antMatchers("/assets/**");
  }


//  public SecurityExpressionHandler<FilterInvocation> securityExpressionHandler(){
//    return new CustomWebSecurityExpressionHandler(
//            new AuthenticationTrustResolverImpl(),"ROLE_"
//    );
//  }

  @Bean
  public AccessDecisionManager accessDecisionManager(){
    List<AccessDecisionVoter<?>> voters = new ArrayList<>();
    voters.add(new WebExpressionVoter());
    voters.add(new OddAdminVoter(new AntPathRequestMatcher("/admin")));
    return new UnanimousBased(voters);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
          .authorizeRequests()
            .antMatchers("/me").hasAnyRole("USER", "ADMIN")
            .antMatchers("/admin").access("hasRole('ADMIN') and isFullyAuthenticated()") // isFullyAuthenticated -> remember me?????? ???????????? ???????????? ?????????.
            .anyRequest().permitAll()
            .accessDecisionManager(accessDecisionManager())
            //.expressionHandler(securityExpressionHandler())
            .and()
          .httpBasic()
            .and()
          .formLogin()
            .defaultSuccessUrl("/")
            .permitAll()
            .and()
          /**
           * remember me ??????
           */
          .rememberMe()
            .key("my-remember-me")
            .rememberMeParameter("remember-me")
            .tokenValiditySeconds(300) // ????????????
            .and()
          /**
           * ???????????? ??????
           */
          .logout()
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // ?????? matcher??? ???????????? logout
            .logoutSuccessUrl("/")
            .invalidateHttpSession(true)
            .clearAuthentication(true)
            .and()
            /**
             * HTTP ????????? HTTPS ???????????? ???????????????
             */
          .requiresChannel()
            .anyRequest().requiresSecure() //?????? ????????? requiresSecure channel(https) ??? ????????????.
            .and()
            /**
             * anonymous ????????? ??????
             */
          .anonymous()
            .principal("thisIsAnonymousUser")
            .authorities("ROLE_ANONYMOUS", "ROLE_UNKNOWN")
            .and()
            /**
             * ?????? ?????? ??????
             */
          .sessionManagement()
            .sessionFixation().changeSessionId() // ?????? ?????? ??????
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // ???????????? ??????
            .invalidSessionUrl("/") // ???????????? ?????? ????????? ?????????????????? ??????????????? ??????
            .maximumSessions(1) // ????????? ?????? ????????? ????????? ??????
              .maxSessionsPreventsLogin(false)
              .and()
            .and()
            /**
             * ???????????? ?????????
             */
          .exceptionHandling()
            .accessDeniedHandler(accessDeniedHandler())

    ;
  }

  public AccessDeniedHandler accessDeniedHandler(){
    return new AccessDeniedHandler() {
      @Override
      public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // ????????? ?????? ????????????
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
package com.prgrms.config;

import com.prgrms.User.UserService;
import com.prgrms.jwt.Jwt;
import com.prgrms.jwt.JwtAuthenticationFilter;
import com.prgrms.jwt.JwtAuthenticationProvider;
import com.prgrms.jwt.JwtSecurityContextRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class WebSecurityConfigure extends WebSecurityConfigurerAdapter {

    private final JwtConfiguration jwtConfiguration;

    public WebSecurityConfigure(JwtConfiguration jwtConfiguration) {
        this.jwtConfiguration = jwtConfiguration;
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/assets/**", "/h2-console/**");
    }


    @Bean
    public Jwt jwt(){
        return new Jwt(
           jwtConfiguration.getIssuer(),
           jwtConfiguration.getClientSecret(),
           jwtConfiguration.getExpirySeconds()
        );
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider(Jwt jwt, UserService userService){
        return new JwtAuthenticationProvider(jwt,userService);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception { //AuthenticationManager bean으로 등록
        return super.authenticationManagerBean();
    }

    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder builder, JwtAuthenticationProvider provider){
        builder.authenticationProvider(provider); // AuthenticationManager에 구현한 provider을 추가해준다.
    }



    private JwtAuthenticationFilter jwtAuthenticationFilter() {// JwtAuthenticationFilter를 생성하는 메소드
        Jwt jwt = getApplicationContext().getBean(Jwt.class);
        return new JwtAuthenticationFilter(jwtConfiguration.getHeader(),jwt);
    }

    public SecurityContextRepository securityContextRepository(){
        Jwt jwt = getApplicationContext().getBean(Jwt.class);
        return new JwtSecurityContextRepository(jwtConfiguration.getHeader(), jwt);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/api/user/me").hasAnyRole("USER", "ADMIN")
                .anyRequest().permitAll()
                .and()
            .csrf()
                .disable()
            .headers()
                .disable()
            .httpBasic()
                .disable()
            .rememberMe()
                .disable()
            .logout()
                .disable()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                /**
                 * 예외처리 핸들러
                 */
            .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
                .and()
            .securityContext()
                .securityContextRepository(securityContextRepository())
                .and()
            .addFilterAfter(jwtAuthenticationFilter(), SecurityContextPersistenceFilter.class)
            //SecurityContextPersistenceFilter 다음 필터에 jwtAuthenticationFilter 추가

        ;
    }

    public AccessDeniedHandler accessDeniedHandler() {
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
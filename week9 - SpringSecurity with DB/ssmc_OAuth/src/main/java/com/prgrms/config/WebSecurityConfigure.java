package com.prgrms.config;

import com.prgrms.User.UserService;
import com.prgrms.jwt.Jwt;
import com.prgrms.jwt.JwtAuthenticationFilter;
import com.prgrms.oauth2.OAuth2AuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class WebSecurityConfigure extends WebSecurityConfigurerAdapter {

    private final JwtConfiguration jwtConfiguration;

    private final UserService userService;

    public WebSecurityConfigure(JwtConfiguration jwtConfiguration, UserService userService) {
        this.jwtConfiguration = jwtConfiguration;
        this.userService = userService;
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/assets/**", "/h2-console/**");
    }


    @Bean
    public Jwt jwt() {
        return new Jwt(
                jwtConfiguration.getIssuer(),
                jwtConfiguration.getClientSecret(),
                jwtConfiguration.getExpirySeconds()
        );
    }


    @Bean
    public OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler(Jwt jwt, UserService userService) {
        return new OAuth2AuthenticationSuccessHandler(jwt, userService);
    }


    private JwtAuthenticationFilter jwtAuthenticationFilter() {// JwtAuthenticationFilter를 생성하는 메소드
        Jwt jwt = getApplicationContext().getBean(Jwt.class);
        return new JwtAuthenticationFilter(jwtConfiguration.getHeader(), jwt);
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
                .oauth2Login()
                .successHandler(getApplicationContext().getBean(OAuth2AuthenticationSuccessHandler.class))
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
package com.prgrms.config;

import com.prgrms.User.UserService;
import com.prgrms.jwt.Jwt;
import com.prgrms.jwt.JwtAuthenticationFilter;
import com.prgrms.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.prgrms.oauth2.OAuth2AuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
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
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository(){
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(JdbcOperations jdbcOperations, ClientRegistrationRepository clientRegistrationRepository){
        return new JdbcOAuth2AuthorizedClientService(jdbcOperations, clientRegistrationRepository);
        // ??????????????? spring ?????????????????? ??????????????? ???????????? bean???????????? ????????? ?????? ????????? ???????????? ??????????????? ????????????.
    }

    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository(OAuth2AuthorizedClientService oAuth2AuthorizedClientService){
        return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(oAuth2AuthorizedClientService); // ??????????????? ???????????? ?????????.. ????????? ????????????.
    }

    @Bean
    public OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler(Jwt jwt, UserService userService) {
        return new OAuth2AuthenticationSuccessHandler(jwt, userService);
    }


    private JwtAuthenticationFilter jwtAuthenticationFilter() {// JwtAuthenticationFilter??? ???????????? ?????????
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
                 * ???????????? ?????????
                 */
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
                .and()
                .oauth2Login()
                .authorizationEndpoint()
                .authorizationRequestRepository(authorizationRequestRepository())
                .and()
                .authorizedClientRepository(getApplicationContext().getBean(AuthenticatedPrincipalOAuth2AuthorizedClientRepository.class)) // ???????????? ????????? ???????????? ?????? ??????????????? ???????????? getBean?????? ???
                .successHandler(getApplicationContext().getBean(OAuth2AuthenticationSuccessHandler.class))
                .and()
                .addFilterAfter(jwtAuthenticationFilter(), SecurityContextPersistenceFilter.class)
        //SecurityContextPersistenceFilter ?????? ????????? jwtAuthenticationFilter ??????

        ;
    }

    public AccessDeniedHandler accessDeniedHandler() {
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
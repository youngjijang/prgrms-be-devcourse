package com.prgrms.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.logging.log4j.util.Strings.isNotEmpty;

public class JwtSecurityContextRepository implements SecurityContextRepository {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final String headerKey;

    private final Jwt jwt;

    public JwtSecurityContextRepository(String headerKey, Jwt jwt) {
        this.headerKey = headerKey;
        this.jwt = jwt;
    }

    /**
     * 헤더에 입력된 token을 가져와서 처리하는 메소드
     */
    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        HttpServletRequest request = requestResponseHolder.getRequest();
        SecurityContext context = SecurityContextHolder.createEmptyContext(); // 빈 SecurityContextHolder 만든다.
        JwtAuthenticationToken authentication = authenticate(request); // JwtAuthenticationToken 만들기
        if (authentication != null) {
            context.setAuthentication(authentication); // SecurityContextHolder에 넣어서 반환
        }
        return context;
    }

    /**
     *  stateless 기반에 api 서비스를 만들 있기때문에 저장해야하는 상태값이 존재하지 않는다.
     */
    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        /*no-op*/
    }

    /**
     *  http 요청이 적당한 security context를 포함하는지 확인하는 메소드
     *  - jwt 토큰을 꺼내와 디코딩하고 토큰 객체를 만드는 과정에 문제가 없다면 true 반환
     */
    @Override
    public boolean containsContext(HttpServletRequest request) {
        JwtAuthenticationToken authentication = authenticate(request);
        return authentication != null;
    }

    /**
     * JwtAuthenticationToken 을 반환하는 메소드
     */
    private JwtAuthenticationToken authenticate(HttpServletRequest request) {
        String token = getToken(request);
        if (isNotEmpty(token)) {
            try {
                Jwt.Claims claims = jwt.verify(token);
                log.debug("Jwt parse result: {}", claims);

                String username = claims.username;
                List<GrantedAuthority> authorities = getAuthorities(claims);
                if (isNotEmpty(username) && authorities.size() > 0) {
                    JwtAuthenticationToken authentication
                            = new JwtAuthenticationToken(new JwtAuthentication(token, username), null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    return authentication;
                }
            } catch (Exception e) {
                log.warn("Jwt processing failed: {}", e.getMessage());
            }
        }
        return null;
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(headerKey);
        if (isNotEmpty(token)) {
            log.debug("Jwt authorization api detected: {}", token);
            try {
                return URLDecoder.decode(token, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }

    private List<GrantedAuthority> getAuthorities(Jwt.Claims claims) {
        String[] roles = claims.roles;
        return (roles == null || roles.length == 0)
                ? emptyList()
                : Arrays.stream(roles).map(SimpleGrantedAuthority::new).collect(toList());
    }

}

package com.prgrms.jwt;

import com.prgrms.User.User;
import com.prgrms.User.UserService;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final Jwt jwt; // jwt 토큰을 만들기 위한

    private final UserService userService; // 인증 처리를 위한

    public JwtAuthenticationProvider(Jwt jwt, UserService userService) {
        this.jwt = jwt;
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // JwtAuthenticationToken 인지 확인 (JwtAuthenticationToken 타입의 token을 처리 가능하다는 의미)
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken jwtAuthentication = (JwtAuthenticationToken) authentication; // 타입 캐스팅
        return processUserAuthentication(
                String.valueOf(jwtAuthentication.getPrincipal()),
                jwtAuthentication.getCredentials()
        );
    }

    /**
     * 실제 인증을 처리하는 메소드
     */
    private Authentication processUserAuthentication(String principal, String credentials) {
        try {
            User user = userService.login(principal, credentials);
            List<GrantedAuthority> authorities = user.getGroup().getAuthorities();
            String token = getToken(user.getLoginId(), authorities);
            JwtAuthenticationToken authenticated =
                    new JwtAuthenticationToken(new JwtAuthentication(token, user.getLoginId()), null, authorities);
            authenticated.setDetails(user); // 인증된 사용자 토큰
            return authenticated;
        } catch (IllegalArgumentException e) { // 비밀번호가 잘못됐을때
            throw new BadCredentialsException(e.getMessage());
        } catch (DataAccessException e) { // 그외 예외처리
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
    }


    /**
     * jwt token을 생성하는 메소드
     */
    private String getToken(String username, List<GrantedAuthority> authorities) {
        String[] roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);
        return jwt.sign(Jwt.Claims.from(username, roles));
    }
}

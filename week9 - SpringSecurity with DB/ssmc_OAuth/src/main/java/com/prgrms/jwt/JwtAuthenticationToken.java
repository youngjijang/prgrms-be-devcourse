package com.prgrms.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;

    private String credentials;

    /**
     * 인증 요청시 호출되는 생성자
     */
    public JwtAuthenticationToken(String principal, String credentials) {
        super(null); // 인증전이기때문에 null
        super.setAuthenticated(false); // 인증되지않음을 표시

        this.principal = principal;
        this.credentials = credentials;
    }

    /**
     * 인증 완료시 호출되는 생성자
     */
    JwtAuthenticationToken(Object principal, String credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        super.setAuthenticated(true); // 인증되었음을 flag true

        this.principal = principal;
        this.credentials = credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public String getCredentials() {
        return credentials;
    }

    /**
     * 인증 여부를 생성자에게 super로 초기화 주었기 때문에, setAuthenticated 직접 호출해서 true 플래그(인증되었음)를 지정할수없도록 막아둔다.
     * 생성자를 통해서만 가능하게 만들어주기 위한 메소드
     */
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        super.setAuthenticated(false);
    }


    /**
     * 비밀번호를 지우는 메소드
     */
    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        credentials = null;
    }

    @Override
    public String toString() {
        return "JwtAuthenticationToken{" +
                "principal=" + principal +
                ", credentials='" + credentials + '\'' +
                '}';
    }
}
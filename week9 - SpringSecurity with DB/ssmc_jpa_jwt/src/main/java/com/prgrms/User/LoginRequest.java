package com.prgrms.User;

public class LoginRequest {
    private String principal;

    private String credentials;

    protected LoginRequest() {}

    public LoginRequest(String principal, String credentials) {
        this.principal = principal;
        this.credentials = credentials;
    }

    public String getPrincipal() {
        return principal;
    }

    public String getCredentials() {
        return credentials;
    }
}

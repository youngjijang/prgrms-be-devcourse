package com.prgrms.jwt;

public class JwtAuthentication { // 인증완료 후에 인증된 사용자를 표시할 객체. principal애 들어갈 객체

    public final String token;

    public final String username;

    JwtAuthentication(String token, String username) {
//        checkArgument(isNotEmpty(token), );
//        checkArgument(isNotEmpty(username), );
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("token must be provided.");
        }
        if (username == null || username.isEmpty()) {
            throw new RuntimeException("username must be provided.");
        }

        this.token = token;
        this.username = username;
    }

    @Override
    public String toString() {
        return "JwtAuthentication{" +
                "token='" + token + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}

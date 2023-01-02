package com.prgrms.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Jwt {

    private final String issuer;

    private final String clientSecret;

    private final int expirySeconds;

    private final Algorithm algorithm;

    private final JWTVerifier jwtVerifier;


    public Jwt(String issuer, String clientSecret, int expirySeconds) {
        this.issuer = issuer;
        this.clientSecret = clientSecret;
        this.expirySeconds = expirySeconds;
        this.algorithm = Algorithm.HMAC512(clientSecret);
        this.jwtVerifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
    }

    public String sign(Claims claims){ // 토큰을 만들어주는 메소드
        Date now = new Date();
        JWTCreator.Builder builder = com.auth0.jwt.JWT.create(); // java jwt에서 제공하는 jwt 생성 메소드
        builder.withIssuer(issuer); // 발행자 정보
        builder.withIssuedAt(now); // 토큰 생성 시간
        if (expirySeconds > 0) {
            builder.withExpiresAt(new Date(now.getTime() + expirySeconds * 1000L)); // ms * 1000
        }
        // 커스텀 claim 저장
        builder.withClaim("username", claims.username);
        builder.withArrayClaim("roles", claims.roles);
        return builder.sign(algorithm); // signature을 주어진 알고리즘으로 저장하여 최종 토큰 생성
    }

    public Claims verify(String token){ //토큰이 주어졌울때 claims로 리턴하는 메소드
        return new Claims(jwtVerifier.verify(token)); // jwtVerifier.verify() -> jwt 토큰의 위변조를 검사하는 로직을 수행
    }

    static public class Claims{ // jwt 토큰 만들거나 검증할때 필요한 데이터를 전달하기위한 inner class
        String username;
        String[] roles;
        Date iat;
        Date exp;

        private Claims(){/*no-op*/} // 기본생성자를 호출할 수 없게 private 으로 감쳐둔다.

        Claims(DecodedJWT decodedJWT){
            Claim username = decodedJWT.getClaim("username");
            if(!username.isNull()){
                this.username = username.asString();
            }
            Claim roles = decodedJWT.getClaim("roles");
            if(!username.isNull()){
                this.roles = roles.asArray(String.class);
            }
            this.iat = decodedJWT.getIssuedAt();
            this.exp = decodedJWT.getExpiresAt();
        }

        public static Claims from(String username, String[] roles){ // 팩토리 메소드
            Claims claims = new Claims();
            claims.username = username;
            claims.roles = roles;
            return claims;
        }

        public Map<String, Object> asMap(){
            Map<String,Object> map = new HashMap<>();
            map.put("username",username);
            map.put("roles", roles);
            map.put("iat",iat());
            map.put("exp",exp());
            return map;
        }

        private long exp() {
            return exp != null ? exp.getTime() : -1;
        }

        private long iat() {
            return iat != null ? iat.getTime() : -1;
        }

        @Override
        public String toString() {
            return "Claims{" +
                    "username='" + username + '\'' +
                    ", roles=" + Arrays.toString(roles) +
                    ", iat=" + iat +
                    ", exp=" + exp +
                    '}';
        }
    }


}

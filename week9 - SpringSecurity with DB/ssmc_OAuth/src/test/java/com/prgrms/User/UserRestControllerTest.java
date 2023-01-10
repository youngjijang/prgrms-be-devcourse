package com.prgrms.User;

import com.prgrms.config.JwtConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.Http2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserRestControllerTest {
    private JwtConfiguration jwtConfiguration;

    private TestRestTemplate testRestTemplate;

    @Autowired
    public void setJwtConfiguration(JwtConfiguration jwtConfiguration) {
        this.jwtConfiguration = jwtConfiguration;
    }

    @Autowired
    public void setTestRestTemplate(TestRestTemplate testRestTemplate) {
        this.testRestTemplate = testRestTemplate;
    }

    @Test
    public void JWT_토큰_테스트(){
        assertEquals(tokenToName(getToken("user")),"user");
        assertEquals(tokenToName(getToken("admin")),"admin");
    }

    private String getToken(String username){
        return testRestTemplate.exchange(
                "/api/user/"+username+"/token",
                HttpMethod.GET,
                null,
                String.class
        ).getBody();
    }

    private String tokenToName(String token){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(jwtConfiguration.getHeader(), token);
        return testRestTemplate.exchange(
                "/api/user/me",
                HttpMethod.GET,
                new HttpEntity<>(httpHeaders),
                String.class
        ).getBody();
    }
}
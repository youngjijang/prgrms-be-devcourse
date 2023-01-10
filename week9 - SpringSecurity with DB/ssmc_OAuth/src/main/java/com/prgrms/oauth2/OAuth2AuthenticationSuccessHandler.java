package com.prgrms.oauth2;

import com.prgrms.User.User;
import com.prgrms.User.UserService;
import com.prgrms.jwt.Jwt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class OAuth2AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler { // 인증이 완료되었을때 호출되는 핸들러
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Jwt jwt;

    private final UserService userService;

    public OAuth2AuthenticationSuccessHandler(Jwt jwt, UserService userService) {
        this.jwt = jwt;
        this.userService = userService;
    }


    // 인증이 완료되고 호출되는 메소드
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws ServletException, IOException {
        /**
         * JWT 토큰 만들어서 응답
         * 사용자를 가입시키는 처리
         */

        if (authentication instanceof OAuth2AuthenticationToken) { //OAuth2AuthenticationToken 인지 확인
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            OAuth2User oAuth2User = oauth2Token.getPrincipal();
            String registrationId = oauth2Token.getAuthorizedClientRegistrationId();

            User user = processUserOAuth2UserJoin(oAuth2User, registrationId);

            // 토큰을 json으로 응답
            String loginSuccessJson = generateLoginSuccessJson(user);
            response.setContentType("application/json;charset=UTF-8");
            response.setContentLength(loginSuccessJson.getBytes(StandardCharsets.UTF_8).length);
            response.getWriter().write(loginSuccessJson);
        } else {
            // OAuth2AuthenticationToken이 아닐경우 아무 처리 하지 않음
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }

    /**
     * join 메소드 호출
     */
    private User processUserOAuth2UserJoin(OAuth2User oAuth2User, String registrationId) {
        return userService.join(oAuth2User, registrationId);
    }

    /**
     * user로 jwt 토큰을 생성하여 json 폼으로 반환
     */
    private String generateLoginSuccessJson(User user) {
        String token = generateToken(user);
        log.debug("Jwt({}) created for oauth2 login user {}", token, user.getUsername());
        return "{\"token\":\"" + token + "\", \"username\":\"" + user.getUsername() + "\", \"group\":\"" + user.getGroup().getName() + "\"}";
    }

    private String generateToken(User user) {
        return jwt.sign(Jwt.Claims.from(user.getUsername(), new String[]{"ROLE_USER"}));
    }

}

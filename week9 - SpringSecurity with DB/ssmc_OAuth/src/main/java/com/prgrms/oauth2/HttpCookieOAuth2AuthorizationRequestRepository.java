package com.prgrms.oauth2;

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.util.SerializationUtils;
import org.springframework.web.util.WebUtils;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

  // 사용할 쿠키 name 지정
  private static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "OAUTH2_AUTHORIZATION_REQUEST";

  private final String cookieName;

  private final int cookieExpireSeconds;

  public HttpCookieOAuth2AuthorizationRequestRepository() {
    this(OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, 180);
  }

  public HttpCookieOAuth2AuthorizationRequestRepository(String cookieName, int cookieExpireSeconds) {
    this.cookieName = cookieName;
    this.cookieExpireSeconds = cookieExpireSeconds;
  }


  /**
   * 쿠키가 존재한다면 OAuth2AuthorizationRequest 조회
   */
  @Override
  public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
    return getCookie(request)
      .map(this::getOAuth2AuthorizationRequest)
      .orElse(null);
  }

  /**
   * 쿠키 만들기
   * OAuth2AuthorizationRequest -> 쿠키
   */
  @Override
  public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
    if (authorizationRequest == null) {
      // 쿠키에 값을 쓸수 없으니 쿠키를 조회해서 존재한다면 쿠키를 clear
      getCookie(request).ifPresent(cookie -> clear(cookie, response));
    } else {
      String value = Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(authorizationRequest)); // 바이트를 string으로 인코드
      Cookie cookie = new Cookie(cookieName, value); // 쿠키에 저장
      cookie.setPath("/");
      cookie.setHttpOnly(true);
      cookie.setMaxAge(cookieExpireSeconds);
      response.addCookie(cookie);
    }
  }

  /**
   * 해당 메소드는 아마 호출이 되지 않을 것이다...
   */
  @Override
  public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
    return loadAuthorizationRequest(request);
  }

  /**
   *  쿠키가 존재한다면 OAuth2AuthorizationRequest를 만든 후 clear
   */
  @Override
  public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
    return getCookie(request)
      .map(cookie -> {
        OAuth2AuthorizationRequest oauth2Request = getOAuth2AuthorizationRequest(cookie);
        clear(cookie, response);
        return oauth2Request;
      })
      .orElse(null);
  }

  /**
   * HttpServletRequest를 통해 쿠키를 가져오는 메소드
   */
  private Optional<Cookie> getCookie(HttpServletRequest request) {
    return ofNullable(WebUtils.getCookie(request, cookieName));
  }

  /**
   * 쿠키 초기화
   */
  private void clear(Cookie cookie, HttpServletResponse response) {
    cookie.setValue("");
    cookie.setPath("/");
    cookie.setMaxAge(0);
    response.addCookie(cookie); // 삭제한 쿠키를 HttpServlet에 저장
  }

  /**
   * 쿠키로 부터 OAuth2AuthorizationRequest를 조회해 오는 메소드. saveAuthorizationRequest와 반대
   * 즉, 쿠키 -> OAuth2AuthorizationRequest
   */
  private OAuth2AuthorizationRequest getOAuth2AuthorizationRequest(Cookie cookie) {
    return (OAuth2AuthorizationRequest) SerializationUtils.deserialize( // 바이트 데이터 Object로 만든다.
      Base64.getUrlDecoder().decode(cookie.getValue()) // 쿠키값(string) 디코드
    );
  }

}
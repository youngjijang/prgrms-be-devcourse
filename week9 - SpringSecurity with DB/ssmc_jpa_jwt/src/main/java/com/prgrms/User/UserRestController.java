package com.prgrms.User;

import com.prgrms.jwt.Jwt;
import com.prgrms.jwt.JwtAuthentication;
import com.prgrms.jwt.JwtAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserRestController {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    public UserRestController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * 보호받는 엔드포인트 - ROLE_USER 또는 ROLE_ADMIN 권한 필요함
     */
    @GetMapping(path = "/user/me")
    public UserDto me(@AuthenticationPrincipal JwtAuthentication authentication) {
        return userService.findByLoginId(authentication.username)
                .map(user ->
                        new UserDto(authentication.token, authentication.username, user.getGroup().getName())
                )
                .orElseThrow(() -> new IllegalArgumentException("Could not found user for " + authentication.username));
    }

    /**
     * 사용자 로그인
     */
    @PostMapping(path = "/user/login")
    public UserDto login(@RequestBody LoginRequest request) {
        // 1. 인증요청을 위한 인증전 token
        JwtAuthenticationToken authToken = new JwtAuthenticationToken(request.getPrincipal(), request.getCredentials());
        // 2. 인증요청 -> Authentication -> getPrincipal로 authentication 꺼내기
        Authentication resultToken = authenticationManager.authenticate(authToken);
        JwtAuthentication authentication = (JwtAuthentication) resultToken.getPrincipal();
        // 3. token detail에 저장한 user 객체 꺼내기
        User user = (User) resultToken.getDetails();
        // 4. user dto 만들어서 반환
        return new UserDto(authentication.token, authentication.username, user.getGroup().getName());
    }

}
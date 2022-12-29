package com.prgrms.User;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        return userRepository.findByLoginId(userName)
                .map(user ->
                        // 찾은 user 객체를 UserDetails 타입으로 반환해준다.
                        User.builder()
                                .username(user.getLoginId())
                                .password(user.getPasswd())
                                .authorities(user.getGroup().getAuthorities())
                                .build()
                )
                .orElseThrow(() -> new UsernameNotFoundException("Could not found user for" + userName));
    }
}

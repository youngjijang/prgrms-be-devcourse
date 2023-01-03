package com.prgrms.User;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public User login(String username, String credentials){
        User user = userRepository.findByLoginId(username)
                .orElseThrow(()-> new UsernameNotFoundException("Could not found user for "+ username));
        user.checkPassword(passwordEncoder, credentials);
        return user;
    }

    @Transactional(readOnly = true)
    public Optional<User> findByLoginId(String loginId) {
        if(loginId.isEmpty()) throw new RuntimeException("loginId must be provided.");
        return  userRepository.findByLoginId(loginId);
    }

}

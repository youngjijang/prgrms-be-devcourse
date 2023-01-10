package com.prgrms.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Optional;

import static org.apache.logging.log4j.util.Strings.isNotEmpty;

@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final UserRepository userRepository;

    private final GroupRepository groupRepository;

    public UserService(UserRepository userRepository, GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        Assert.isTrue(isNotEmpty(username), "username must be provided.");

        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByProviderAndProviderId(String provider, String providerId) {
        Assert.isTrue(isNotEmpty(provider), "provider must be provided.");
        Assert.isTrue(isNotEmpty(providerId), "providerId must be provided.");

        return userRepository.findByProviderAndProviderId(provider, providerId);
    }


    @Transactional
    public User join(OAuth2User oauth2User, String provider) { // OAuth 인증된 user의 파라미터값 , 헤당 인증이 어떤 provider 인지
        Assert.isTrue(oauth2User != null, "oauth2User must be provided.");
        Assert.isTrue(isNotEmpty(provider), "provider must be provided.");

        /**
         * user 생성을 위해 필요한 필드값
         * username - 카카오 닉네임
         * provider - provider 파라미터
         * providerId - oauth2User.getName() : 인증된 user의 고유 식별키
         * profileImage - 카카오 프로필
         * group - USER_GROUP Group
         */

        String providerId = oauth2User.getName();
        return findByProviderAndProviderId(provider, providerId) // 사용자 조회
                .map(user -> {
                    log.warn("Already exists: {} for (provider: {}, providerId: {})", user, provider, providerId);
                    return user;
                })
                .orElseGet(() -> {
                    Map<String, Object> attributes = oauth2User.getAttributes();
                    @SuppressWarnings("unchecked")
                    Map<String, Object> properties = (Map<String, Object>) attributes.get("properties"); // 사용자 정보 꺼내오기
                    Assert.isTrue(properties != null, "OAuth2User properties is empty");

                    String nickname = (String) properties.get("nickname");
                    String profileImage = (String) properties.get("profile_image");
                    Group group = groupRepository.findByName("USER_GROUP")
                            .orElseThrow(() -> new IllegalStateException("Could not found group for USER_GROUP"));
                    return userRepository.save(
                            new User(nickname, provider, providerId, profileImage, group) // 가입
                    );
                });
    }

}

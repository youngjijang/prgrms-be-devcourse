package com.prgrms.User;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.apache.logging.log4j.util.Strings.isNotEmpty;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "provider")
    private String provider;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "profile_image")
    private String profileImage;

    @ManyToOne(optional = false)
    @JoinColumn(name = "group_id")
    private Group group;

    protected User() {/*no-op*/}

    public User(String username, String provider, String providerId, String profileImage, Group group) {
        Assert.isTrue(isNotEmpty(username), "username must be provided.");
        Assert.isTrue(isNotEmpty(provider), "provider must be provided.");
        Assert.isTrue(isNotEmpty(providerId), "providerId must be provided.");
        Assert.isTrue(group != null, "group must be provided.");

        this.username = username;
        this.provider = provider;
        this.providerId = providerId;
        this.profileImage = profileImage;
        this.group = group;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public Optional<String> getProfileImage() {
        return ofNullable(profileImage);
    }

    public Group getGroup() {
        return group;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", provider='" + provider + '\'' +
                ", providerId='" + providerId + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", group=" + group +
                '}';
    }
}
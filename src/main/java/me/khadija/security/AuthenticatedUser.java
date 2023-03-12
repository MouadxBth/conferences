package me.khadija.security;

import com.vaadin.flow.spring.security.AuthenticationContext;
import me.khadija.mappers.UserMapper;
import me.khadija.models.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthenticatedUser {

    private final UserMapper userMapper;
    private final AuthenticationContext authenticationContext;

    public AuthenticatedUser(AuthenticationContext authenticationContext,
                             UserMapper userMapper) {
        this.userMapper = userMapper;
        this.authenticationContext = authenticationContext;
    }

    public Optional<User> get() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .map(userDetails -> userMapper.find(userDetails.getUsername()));
    }

    public void logout() {
        authenticationContext.logout();
    }

}

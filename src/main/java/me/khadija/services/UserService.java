package me.khadija.services;

import me.khadija.mappers.UserMapper;
import me.khadija.models.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User result = userMapper.find(username);

        if (result == null)
            throw new UsernameNotFoundException("Could not find user with the username: " + username);
        return new org.springframework.security.core.userdetails.User(result.getUsername(),
                result.getHashedPassword(),
                result.getEnabled(),
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    public User fetch(String username) {
        return userMapper.find(username);
    }

    public long fetchId(String username) {
        return userMapper.findId(username);
    }

    public User fetchByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    public List<User> fetchAll() {
        return userMapper.findAll();
    }

    public Optional<User> find(String username) {
        return Optional.ofNullable(userMapper.find(username));
    }

    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userMapper.findByEmail(email));
    }

    public void save(User user) {
        find(user.getUsername()).ifPresentOrElse($ -> {
            throw new IllegalStateException("User with the username " + user.getUsername() + " already exists!");
        }, () -> userMapper.insert(user));
    }

    public void update(User user) {
        find(user.getUsername()).ifPresentOrElse($ -> userMapper.update(user), () -> {
            throw new IllegalStateException("User with the username " + user.getUsername() + " doesnt exist!");
        });
    }

    public User update(String username,
                       String hashedPassword,
                       String firstName,
                       String lastName,
                       String email,
                       Boolean enabled) {

        final User user = fetch(username);

        if (user == null)
            return null;

        if (hashedPassword != null && !hashedPassword.isBlank()) {
            user.setHashedPassword(hashedPassword);
        }
        if (firstName != null && !firstName.isBlank()) {
            user.setFirstName(firstName);
        }
        if (lastName != null && !lastName.isBlank()) {
            user.setLastName(lastName);
        }
        if (email != null && !email.isBlank()) {
            findByEmail(email).ifPresentOrElse($ -> {
                throw new IllegalStateException("Email " + email + " is already taken!");
            }, () -> user.setEmail(email));
        }
        if (enabled != null) {
            user.setEnabled(enabled);
        }
        userMapper.update(user);
        return user;
    }

    public User delete(String username) {
        final User user = fetch(username);
        if (user == null)
            return null;
        userMapper.delete(user);
        return user;
    }

    public void createTable() {
        userMapper.createTableIfNotExists();
    }

    public void dropTable() {
        userMapper.dropTableIfExists();
    }
}

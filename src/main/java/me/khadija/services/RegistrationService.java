package me.khadija.services;

import me.khadija.email.EmailSender;
import me.khadija.models.ConfirmationToken;
import me.khadija.models.User;
import me.khadija.registration.RegistrationRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static me.khadija.utilities.Utilities.buildEmail;

@Service
public class RegistrationService {

    private final UserService userService;
    private final ConfirmationTokenService confirmationTokenService;

    private final EmailSender emailSender;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserService userService,
                               ConfirmationTokenService confirmationTokenService,
                               EmailSender emailSender,
                               PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.confirmationTokenService = confirmationTokenService;
        this.emailSender = emailSender;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean register(RegistrationRequest request) {
        System.out.println(passwordEncoder.encode(request.password()));
        final User user = new User(null, request.username(),
                passwordEncoder.encode(request.password()),
                request.firstName(),
                request.lastName(),
                request.email(),
                false);
        if (userService.find(user.getUsername()).isPresent()) {
            return false;
        }

        if (userService.fetchByEmail(user.getEmail()) != null) {
            return false;
        }

        final ConfirmationToken confirmationToken = new ConfirmationToken(null,
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10),
                null,
                user
        );

        userService.save(user);
        user.setId(userService.fetchId(user.getUsername()));

        System.out.println(user);

        confirmationToken.setUser(user);
        confirmationTokenService.save(confirmationToken);

        String link = "http://localhost:8080/login/" + confirmationToken.getToken();
        emailSender.send(
                request.email(),
                buildEmail(request.firstName(), link));
        return true;
    }

    public boolean confirm(String token) {
        final Optional<ConfirmationToken> optional = confirmationTokenService.find(token);
        optional.ifPresent(confirmationToken -> {
            if (confirmationToken.getConfirmedAt() != null) {
                throw new IllegalStateException("Email already confirmed!");
            }
            LocalDateTime expiredAt = confirmationToken.getExpiresAt();

            if (expiredAt.isBefore(LocalDateTime.now())) {
                throw new IllegalStateException("Confirmation token expired!");
            }
            if (confirmationToken.getUser() == null) {
                throw new IllegalStateException("Invalid token!");
            }
            confirmationToken.setConfirmedAt(LocalDateTime.now());
            confirmationTokenService.update(confirmationToken);

            confirmationToken.getUser().setEnabled(true);
            userService.update(confirmationToken.getUser());

            confirmationTokenService.fetch(confirmationToken.getUser())
                            .forEach(confirmationTokenService::delete);
        });
        return optional.isPresent();
    }

}

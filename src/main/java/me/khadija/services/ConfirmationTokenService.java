package me.khadija.services;

import me.khadija.mappers.ConfirmationTokenMapper;
import me.khadija.models.ConfirmationToken;
import me.khadija.models.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConfirmationTokenService {
    private final ConfirmationTokenMapper confirmationTokenMapper;

    public ConfirmationTokenService(ConfirmationTokenMapper confirmationTokenMapper) {
        this.confirmationTokenMapper = confirmationTokenMapper;
    }

    public List<ConfirmationToken> fetch(User user) {
        return confirmationTokenMapper.findUser(user);
    }

    public List<ConfirmationToken> fetchAll() {
        return confirmationTokenMapper.findAll();
    }

    public Optional<ConfirmationToken> find(String token) {
        return Optional.ofNullable(confirmationTokenMapper.find(token));
    }

    public void save(ConfirmationToken confirmationToken) {
        find(confirmationToken.getToken()).ifPresentOrElse($ -> {
            throw new IllegalStateException("Confirmation Token with the token " + confirmationToken.getToken() + " doesnt exist!");

        }, () -> confirmationTokenMapper.insert(confirmationToken));
    }

    public void update(ConfirmationToken confirmationToken) {
        find(confirmationToken.getToken()).ifPresentOrElse(confirmationTokenMapper::update, () -> {
            throw new IllegalStateException("Confirmation Token with the token " + confirmationToken.getToken() + " doesnt exist!");
        });
    }

    public void delete(ConfirmationToken confirmationToken) {
        find(confirmationToken.getToken()).ifPresentOrElse(confirmationTokenMapper::delete, () -> {
            throw new IllegalStateException("Confirmation Token with the token " + confirmationToken.getToken() + " doesnt exist!");
        });
    }

    public void createTable() {
        confirmationTokenMapper.createTableIfNotExists();
    }

    public void dropTable() {
        confirmationTokenMapper.dropTableIfExists();
    }
}

package me.khadija;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;
import me.khadija.services.ConferenceService;
import me.khadija.services.ConfirmationTokenService;
import me.khadija.services.UserConferenceService;
import me.khadija.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme(value = "conferences")
@Push
public class Application implements AppShellConfigurator, CommandLineRunner {

    private final UserService userService;
    private final ConferenceService conferenceService;
    private final UserConferenceService userConferenceService;
    private final ConfirmationTokenService confirmationTokenService;

    public Application(UserService userService,
                       ConferenceService conferenceService,
                       UserConferenceService userConferenceService,
                       ConfirmationTokenService confirmationTokenService) {
        this.userService = userService;
        this.conferenceService = conferenceService;
        this.userConferenceService = userConferenceService;
        this.confirmationTokenService = confirmationTokenService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
       // dropTables();
        createTables();
    }

    private void createTables() {
        userService.createTable();
        conferenceService.createTable();
        userConferenceService.createTable();
        confirmationTokenService.createTable();
    }

    private void dropTables() {
        confirmationTokenService.dropTable();
        userConferenceService.dropTable();
        conferenceService.dropTable();
        userService.dropTable();
    }
}

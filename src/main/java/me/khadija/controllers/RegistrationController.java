package me.khadija.controllers;

import me.khadija.models.User;
import me.khadija.registration.RegistrationRequest;
import me.khadija.services.RegistrationService;
import me.khadija.services.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("auth")

public class RegistrationController {

    private final RegistrationService registrationService;
    private final UserService userService;

    public RegistrationController(RegistrationService registrationService, UserService userService) {
        this.registrationService = registrationService;
        this.userService = userService;
    }

    @PostMapping(path = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_HTML_VALUE)
    public String register(@RequestBody RegistrationRequest request) {
        System.out.println("CALLED§");
        return registrationService.register(request) + "";
    }

    @GetMapping(path = "/confirm")
    public String confirm(@RequestParam("token") String token) {
        final String result = registrationService.confirm(token) ? "<h1>CONFIRMED<h1>" : "<h1>NOT CONFIRMED<h1>";
        System.out.println(result);
        return result;
    }

    @GetMapping(path = "/is_confirmed")
    public String isConfirmed(@RequestParam("username") String username) {
        final boolean result = userService.find(username).stream().anyMatch(User::getEnabled);
        return result ? "CONFIRMED" : "NOT CONFIRMED";
    }

    @GetMapping(path = "/confirmed")
    public List<User> confirmed() {
        return userService.fetchAll().stream().filter(User::getEnabled)
                .toList();
    }

    @GetMapping("/test")
    public String hello() {
        return "Hello";
    }
}

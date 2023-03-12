package me.khadija.controllers;

import me.khadija.models.User;
import me.khadija.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping(path = {"/all", "/", ""})
    public ResponseEntity<List<User>> all(){
        return ResponseEntity.ok(userService.fetchAll());
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<User> byUsername(@PathVariable("username") String username){
        final User user = userService.fetch(username);
        if (user == null)
            return ResponseEntity.notFound()
                    .header("error",
                            "User with the username " + username + " doesnt exist!")
                    .build();

        return ResponseEntity.ok(user);
    }

    @GetMapping("/find_email/{email}")
    public ResponseEntity<User> byEmail(@PathVariable("email") String email){
        final User user = userService.fetchByEmail(email);
        if (user == null)
            return ResponseEntity.notFound()
                    .header("error",
                            "User with the email " + email + " doesnt exist!")
                    .build();

        return ResponseEntity.ok(user);
    }

    @PutMapping("/update/{username}")
    public ResponseEntity<User> update(
            @PathVariable("username") String username,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) Boolean enabled) {
        final User user = userService.update(username,
                firstName,
                lastName,
                email,
                passwordEncoder.encode(password),
                enabled);
        if (user == null)
            return ResponseEntity.notFound()
                    .header("error",
                            "User with the username " + username + " doesnt exist!")
                    .build();
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<User> delete(@PathVariable("username")String username ){
        final User user = userService.delete(username);
        if (user == null)
            return ResponseEntity.notFound()
                    .header("error",
                            "User with the username " + username + " doesnt exist!")
                    .build();
        return ResponseEntity.ok(user);
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
//        System.out.println(username + " " + password);
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(username, password)
//        );
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        System.out.println("AUTHENTICATED " + authentication);
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        return ResponseEntity.ok(new LoginResponse(userDetails.getUsername(), userDetails.getAuthorities()));
//    }
//
//    @PostMapping("/logout")
//    public void logout() {
//
//    }

}

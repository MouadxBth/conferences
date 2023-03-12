package me.khadija.controllers;

import me.khadija.models.Conference;
import me.khadija.models.User;
import me.khadija.services.ConferenceService;
import me.khadija.services.UserConferenceService;
import me.khadija.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Set;

@RestController
public class UserConferenceController {

    private final UserService userService;
    private final ConferenceService conferenceService;
    private final UserConferenceService userConferenceService;

    public UserConferenceController(UserService userService,
                                    ConferenceService conferenceService,
                                    UserConferenceService userConferenceService) {
        this.userService = userService;
        this.conferenceService = conferenceService;
        this.userConferenceService = userConferenceService;
    }

    @PostMapping("/join/{conference}")
    public void joinConference(
            @PathVariable("conference") String conferenceName,
            @RequestParam String username) {
        userConferenceService.addUserToConference(username, conferenceName);
    }

    @PostMapping("/leave/{conference}")
    public void leaveConference(
            @PathVariable("conference") String conferenceName,
            @RequestParam String username) {
        userConferenceService.removeUserFromConference(username, conferenceName);
    }

    @PostMapping("/conferences/add/")
    public void addConference(
            @RequestParam String name,
            @RequestParam String owner,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer member_limit,
            @RequestParam(required = false) Boolean privateConference,
            @RequestParam(required = false) LocalDateTime createdAt,
            @RequestParam(required = false) LocalDateTime startsAt,
            @RequestParam(required = false) Boolean started) {
        userConferenceService.addConference(name,
                owner,
                title,
                description,
                member_limit,
                privateConference,
                createdAt,
                startsAt,
                started);
    }

    @GetMapping("/joined_conferences/{username}")
    public ResponseEntity<Set<Conference>> joinedConferences(@PathVariable("username") String username) {
        final User user = userService.fetch(username);
        if (user == null)
            return ResponseEntity.notFound()
                    .header("error", "User doesnt exist")
                    .build();

        return ResponseEntity.ok(userConferenceService.findConferences(user));
    }

    @GetMapping("/conferences/{username}")
    public ResponseEntity<Set<Conference>> conferences(@PathVariable("username") String username) {
        final User user = userService.fetch(username);
        if (user == null)
            return ResponseEntity.notFound()
                    .header("error", "User doesnt exist")
                    .build();

        return ResponseEntity.ok(userConferenceService.createdConferences(user));
    }

    @GetMapping("/members/{name}")
    public ResponseEntity<Set<User>> members(@PathVariable("name") String name) {
        final Conference conference = conferenceService.fetch(name);
        if (conference == null)
            return ResponseEntity.notFound()
                    .header("error", "Conference doesnt exist")
                    .build();

        return ResponseEntity.ok(userConferenceService.findMembers(conference));
    }

    @PutMapping("/conferences/update/{name}")
    public void updateConference(
            @PathVariable("name") String name,
            @RequestParam(required = false) String owner,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer member_limit,
            @RequestParam(required = false) Boolean privateConference,
            @RequestParam(required = false) LocalDateTime createdAt,
            @RequestParam(required = false) LocalDateTime startsAt,
            @RequestParam(required = false) Boolean started) {

        userConferenceService.removeOwnerFromConference(name);
        conferenceService.update(name,
                owner,
                title,
                description,
                member_limit,
                privateConference,
                createdAt,
                startsAt,
                started);
        userConferenceService.addOwnerToConference(name);
    }


}

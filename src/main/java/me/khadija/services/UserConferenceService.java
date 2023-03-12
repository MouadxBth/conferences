package me.khadija.services;

import me.khadija.mappers.UserConferenceMapper;
import me.khadija.models.Conference;
import me.khadija.models.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserConferenceService {

    private final UserConferenceMapper userConferenceMapper;
    private final UserService userService;
    private final ConferenceService conferenceService;

    public UserConferenceService(UserConferenceMapper userConferenceMapper, UserService userService, ConferenceService conferenceService) {
        this.userConferenceMapper = userConferenceMapper;
        this.userService = userService;
        this.conferenceService = conferenceService;
    }

    public Set<User> findMembers(Conference conference) {
        return userConferenceMapper.findMembers(conference);
    }

    public Set<Conference> findConferences(User user) {
        return userConferenceMapper.findConferences(user);
    }

    public Set<Conference> createdConferences(User user) {
        return conferenceService.fetchAll()
                .stream()
                .filter(conference -> conference.getOwner().getUsername().equals(user.getUsername()))
                .collect(Collectors.toSet());
    }

    public void addConference(Conference conference) {

        conferenceService.find(conference.getName())
                .ifPresent($ -> {throw new IllegalStateException("Conference " + conference.getName() + " Already exists!");});

        final User user = conference.getOwner();

        if (user == null) {
            throw new IllegalStateException("Could not find the owner of " + conference.getName() + " cannot set owner null");
        }

        if (!user.getEnabled())
            throw new IllegalStateException("User " + user.getUsername() + " has not confirmed his email yet!");

        conference.setMember_limit(conference.getMember_limit() == null || conference.getMember_limit() == 0 ? -1 : conference.getMember_limit());
        conference.setPrivateConference(conference.getPrivateConference() != null && conference.getPrivateConference());
        conference.setCreatedAt(conference.getCreatedAt() == null ? LocalDateTime.now() : conference.getCreatedAt());
        conference.setStarted(conference.getStarted() != null && conference.getStarted());

        conferenceService.save(conference);

        addUserToConference(user.getUsername(), conference.getName());
    }

    public void addConference(String name, String owner, String title, String description,
            Integer member_limit,
            Boolean privateConference,
            LocalDateTime createdAt,
            LocalDateTime startsAt,
            Boolean started) {

        conferenceService.find(name)
                .ifPresent($ -> {throw new IllegalStateException("Conference " + name + " Already exists!");});

        final User user = userService.find(owner)
                .orElseThrow(() -> new IllegalStateException("Could not find user with the username " + owner + " cannot set owner null"));

        if (!user.getEnabled())
            throw new IllegalStateException("User " + owner + " has not confirmed his email yet!");

        conferenceService.save(new Conference(null, name,
                user,
                title,
                description,
                member_limit == null || member_limit == 0 ? -1 : member_limit,
                privateConference != null && privateConference,
                createdAt == null ? LocalDateTime.now() : createdAt,
                startsAt,
                started != null && started));

        addUserToConference(name, owner);
    }

    public void addUserToConference(User user, Conference conference) {
        userConferenceMapper.insert(user, conference);
    }

    public void removeUserFromConference(User user, Conference conference) {
        userConferenceMapper.delete(user, conference);
    }

    public void addUserToConference(String username, String name) {
        final Conference conference = conferenceService.find(name)
                .orElseThrow(() -> new IllegalStateException("Could not find conference with the name " + name));

        final User user = userService.find(username)
                .orElseThrow(() -> new IllegalStateException("Could not find user with the username " + username));

        if (!user.getEnabled())
            throw new IllegalStateException("User " + username + " has not confirmed his email yet!");

        if (isInConference(user, conference))
            throw new IllegalStateException("User " + username + " is already in conference " + name);

        if (conference.getMember_limit() > 0
                && findMembers(conference).size() >= conference.getMember_limit()) {
            throw new IllegalStateException("Cannot exceed the conference member limit");
        }

        userConferenceMapper.insert(user, conference);
    }

    public void removeUserFromConference(String username, String name) {
        final User user = userService.find(username)
                .orElseThrow(() -> new IllegalStateException("User with the username " + username + " doesnt exist!"));
        final Conference conference = conferenceService.find(name)
                .orElseThrow(() -> new IllegalStateException("Conference with the name " + name + " doesnt exist!"));

        if (!isInConference(user, conference))
            throw new IllegalStateException("User " + username + " is not in conference " + name);
        userConferenceMapper.delete(user, conference);
    }

    public void addOwnerToConference(String name) {
        final Conference conference = conferenceService.find(name)
                .orElseThrow(() -> new IllegalStateException("Conference with the name " + name + " doesnt exist!"));
        if (isInConference(conference.getOwner(), conference))
            return;
        userConferenceMapper.insert(conference.getOwner(), conference);
    }

    public void removeOwnerFromConference(String name) {
        final Conference conference = conferenceService.find(name)
                .orElseThrow(() -> new IllegalStateException("Conference with the name " + name + " doesnt exist!"));
        System.out.println("OWNER " + conference.getOwner());
        if (!isInConference(conference.getOwner(), conference)) {
            System.out.println("NOT IN CONFERENCE");
            return;
        }

        userConferenceMapper.delete(conference.getOwner(), conference);
    }

    public boolean isInConference(User user, Conference conference) {
        if (user == null || conference == null)
            return false;
        return findConferences(user).stream()
                .anyMatch(c -> c.getName().equals(conference.getName()));
    }

    public void createTable() {
        userConferenceMapper.createTableIfNotExists();
    }

    public void dropTable() {
        userConferenceMapper.dropTableIfExists();
    }
}

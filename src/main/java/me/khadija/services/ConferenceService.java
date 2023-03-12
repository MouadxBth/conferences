package me.khadija.services;

import me.khadija.mappers.ConferenceMapper;
import me.khadija.mappers.UserMapper;
import me.khadija.models.Conference;
import me.khadija.models.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ConferenceService {

    private final ConferenceMapper conferenceMapper;
    private final UserMapper userMapper;

    public ConferenceService(ConferenceMapper conferenceMapper, UserMapper userMapper) {
        this.conferenceMapper = conferenceMapper;
        this.userMapper = userMapper;
    }

    public Conference fetch(String name) {
        return conferenceMapper.find(name);
    }

    public List<Conference> fetchAll() {
        return conferenceMapper.findAll();
    }

    public Optional<Conference> find(String name) {
        return Optional.ofNullable(conferenceMapper.find(name));
    }

    public Optional<Conference> find(long id) {
        return Optional.ofNullable(conferenceMapper.findById(id));
    }

    public void save(Conference conference) {
        find(conference.getName()).ifPresentOrElse($ -> {
            throw new IllegalStateException("Conference with the name " + conference.getName() + " already exists!");
        }, () -> conferenceMapper.insert(conference));
    }

    public void update(Conference conference) {
        if (conference.getId() != null) {
            find(conference.getId()).ifPresentOrElse($ -> {
                System.out.println("UPDATING!!");
                conferenceMapper.update(conference);
            }, () -> {
                find(conference.getName()).ifPresentOrElse($ -> conferenceMapper.update(conference),
                        () -> {
                            throw new IllegalStateException("Conference with the name " + conference.getName() + " doesnt exist!");
                        });
            });
        }

    }

    public Conference update(String name,
                             String ownerUsername,
                             String title,
                             String description,
                             Integer member_limit,
                             Boolean privateConference,
                             LocalDateTime createdAt,
                             LocalDateTime startsAt,
                             Boolean started) {
        final Conference conference = fetch(name);

        if (conference == null)
            return null;

        if (ownerUsername != null && !ownerUsername.isBlank()) {
            final User user = userMapper.find(ownerUsername);
            if (user == null)
                throw new IllegalStateException("User with the username " + ownerUsername + " doesnt exist!");
            if (!user.getEnabled())
                throw new IllegalStateException("User with the username " + ownerUsername + " hasn't confirmed his email yet!");
            conference.setOwner(userMapper.find(ownerUsername));
        }
        if (title != null && !title.isBlank()) {
            conference.setTitle(title);
        }
        if (description != null && !description.isBlank()) {
            conference.setDescription(description);
        }
        if (member_limit != null) {
            if (member_limit == 0)
                throw new IllegalStateException("Cannot update the member limit to 0");
            else
                conference.setMember_limit(member_limit);
        }
        if (privateConference != null)
            conference.setPrivateConference(privateConference);
        if (createdAt != null)
            conference.setCreatedAt(createdAt);
        if (startsAt != null)
            conference.setStartsAt(startsAt);
        if (started != null)
            conference.setStarted(started);
        conferenceMapper.update(conference);
        return conference;
    }

    public Conference delete(String name) {
        final Conference conference = fetch(name);
        if (conference == null)
            return null;
        conferenceMapper.delete(conference);
        return conference;
    }

    public void createTable() {
        conferenceMapper.createTableIfNotExists();
    }

    public void dropTable() {
        conferenceMapper.dropTableIfExists();
    }
}

package me.khadija.views.conference;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import me.khadija.models.Conference;
import me.khadija.models.User;
import me.khadija.services.UserService;

import java.util.stream.Stream;

import static me.khadija.utilities.Utilities.*;

public class ConferenceForm extends FormLayout {
    private TextField name;
    private TextField owner;
    private TextField title;
    private TextArea description;
    private NumberField member_limit;
    private Checkbox privateConference;
    private DateTimePicker startsAt;
    private final Binder<Conference> binder = new Binder<>();
    private final UserService userService;

    public ConferenceForm(UserService userService) {
        this.userService = userService;

        createFields();
        setupFields();
        setupBinds();
        add(name,
                owner,
                member_limit,
                title,
                privateConference,
                startsAt,
                description);
    }


    private void createFields() {
        name = createInputField();
        title = createInputField();
        owner = createInputField();
        description = new TextArea();
        member_limit = createNumberField();
        privateConference = new Checkbox();
        startsAt = new DateTimePicker();
    }

    private void setupFields() {
        name.setLabel("Name:");
        name.setPlaceholder("Conference's name");

        owner.setLabel("Owner:");
        owner.setPlaceholder("Conference's owner");

        title.setLabel("Title:");
        title.setPlaceholder("Conference's custom title");

        member_limit.setLabel("Members limit:");
        member_limit.setPlaceholder("Conference's members limit");

        privateConference.setLabel("Private:");

        startsAt.setLabel("Starts at:");

        description.setLabel("Description:");
        description.setMaxLength(4096);
        description.setPlaceholder("Conference's description");
        description.setValueChangeMode(ValueChangeMode.EAGER);
        description.addValueChangeListener(e -> e.getSource()
                .setHelperText(e.getValue().length() + "/" + 4096));
    }

    private void setupBinds() {
        binder.forField(name).bind(Conference::getName, Conference::setName);
        binder.forField(title).bind(Conference::getTitle, Conference::setTitle);
        binder.forField(owner).bind(conference -> {
            final User user = conference.getOwner();
            return user == null ? null : user.getUsername();
        }, (conference, newOwner) -> {
            final User user = userService.fetch(newOwner);
            if (user == null)
                return;
            if (userService.find(newOwner).isEmpty()) {
                final Notification notification = createNotification(newOwner + " was not found!");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setDuration(3 * 1000);
                notification.open();
                return;
            }
            conference.setOwner(user);
        });
        binder.forField(description).bind(Conference::getDescription, Conference::setDescription);
        binder.forField(startsAt).bind(Conference::getStartsAt, Conference::setStartsAt);
        binder.forField(privateConference).bind(Conference::getPrivateConference, Conference::setPrivateConference);

        binder.forField(member_limit)
                .bind(conference -> conference.getMember_limit() == null ? null : conference.getMember_limit().doubleValue(),
                        (conference, value) -> conference.setMember_limit(value == null ? null : value.intValue()));
    }

    public void clear() {
        Stream.of(name, owner,
                title,member_limit,
                description,
                privateConference,
                startsAt
                ).forEach(HasValue::clear);
    }

    public Conference getInputConference() {
        final Conference conference = new Conference();

        if (name.getValue() != null && !name.getValue().isBlank()) {
            conference.setName(name.getValue());
        }
        if (owner.getValue() != null && !owner.getValue().isBlank()) {
            userService.find(owner.getValue()).ifPresent(conference::setOwner);
        }
        if (title.getValue() != null && !title.getValue().isBlank()) {
            conference.setTitle(name.getValue());
        }
        if (description.getValue() != null && !description.getValue().isBlank()) {
            conference.setDescription(name.getValue());
        }
        if (member_limit.getValue() != null) {
            conference.setMember_limit(member_limit.getValue().intValue());
        }
        if (privateConference.getValue() != null) {
            conference.setPrivateConference(privateConference.getValue());
        }
        return conference;
    }

    public Binder<Conference> getBinder() {
        return binder;
    }

    public TextArea getDescription() {
        return description;
    }
}

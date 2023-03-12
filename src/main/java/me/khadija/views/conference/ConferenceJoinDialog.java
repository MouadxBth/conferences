package me.khadija.views.conference;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import me.khadija.models.Conference;
import me.khadija.models.User;
import me.khadija.security.AuthenticatedUser;
import me.khadija.services.ConferenceService;
import me.khadija.services.UserConferenceService;
import me.khadija.services.UserService;

import java.util.Set;

public class ConferenceJoinDialog extends Dialog {

    private final AuthenticatedUser authenticatedUser;
    private final UserConferenceService userConferenceService;
    private final Conference conference;
    private TextField title,
            memberCount;
    private VerticalLayout membersLayout;
    private Grid<User> members;
    private DateTimePicker startsAt;
    private Button joinLeaveButton,
            cancelButton;
    private FormLayout formLayout;

    public ConferenceJoinDialog(UserConferenceService userConferenceService,
                                Conference conference,
                                AuthenticatedUser authenticatedUser) {
        this.userConferenceService = userConferenceService;
        this.authenticatedUser = authenticatedUser;
        this.conference = conference;

        createComponents();
        setupComponents();

        add(formLayout);

        getFooter().add(cancelButton,
                joinLeaveButton);
    }

    private void createComponents() {
        title = new TextField(conference.getTitle());
        memberCount = new TextField();
        members = new Grid<>();
        startsAt = new DateTimePicker();
        joinLeaveButton = new Button();
        cancelButton = new Button("Cancel");
        formLayout = new FormLayout();
        membersLayout = new VerticalLayout();
    }

    private void setupComponents() {
        final Set<User> conferenceMembers = userConferenceService.findMembers(conference);
        title.setLabel("Title:");
        title.setValue(conference.getTitle());
        title.setReadOnly(true);

        memberCount.setLabel("Current members:");
        memberCount.setReadOnly(true);
        memberCount.setValue(conference.getMember_limit() <= 0 ?
                conferenceMembers.size() + "" : conferenceMembers.size() + "/" + conference.getMember_limit());


        membersLayout.add(new Label("Members:"));
        members.addColumn(User::getFirstName).setHeader("First name");
        members.addColumn(User::getLastName).setHeader("Last name");
        members.setItems(conferenceMembers);
        membersLayout.add(members);

        startsAt.setLabel("Starts at:");
        startsAt.setReadOnly(true);
        startsAt.setValue(conference.getStartsAt());

        authenticatedUser.get().ifPresent(user -> {
            if (userConferenceService.isInConference(user, conference)) {
                joinLeaveButton.setText("Leave");
                joinLeaveButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
                joinLeaveButton.addClickListener(event -> {
                    userConferenceService.removeUserFromConference(user, conference);
                    close();
                });
            } else {
                joinLeaveButton.setText("Join");
                joinLeaveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                joinLeaveButton.addClickListener(event -> {
                    userConferenceService.addUserToConference(user, conference);
                    close();
                });
            }
        });
        cancelButton.addClickListener($ -> close());

        formLayout.add(title, memberCount, startsAt, membersLayout);

        formLayout.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new FormLayout.ResponsiveStep("500px", 3));
        formLayout.setColspan(membersLayout, 3);
    }


}

package me.khadija.views.conference;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import me.khadija.models.Conference;
import me.khadija.security.AuthenticatedUser;
import me.khadija.services.ConferenceService;
import me.khadija.services.UserConferenceService;
import me.khadija.services.UserService;

import java.time.Duration;
import java.time.LocalDateTime;

public class ConferenceView extends HorizontalLayout {

    private final Conference conference;
    private final UserConferenceService userConferenceService;
    private final UserService userService;
    private final ConferenceService conferenceService;

    private final AuthenticatedUser authenticatedUser;
    private VerticalLayout container;
    private HorizontalLayout header;
    private Span title, createdAt, startsAt, owner, members;
    private Paragraph description;
    private HorizontalLayout footer;

    public ConferenceView(Conference conference,
                          UserConferenceService userConferenceService,
                          UserService userService,
                          ConferenceService conferenceService, AuthenticatedUser authenticatedUser) {
        this.conference = conference;
        this.userConferenceService = userConferenceService;
        this.userService = userService;
        this.conferenceService = conferenceService;
        this.authenticatedUser = authenticatedUser;

        addClassNames(LumoUtility.Padding.Horizontal.MEDIUM,
                LumoUtility.BoxShadow.MEDIUM);

        createComponents(conference);
        setupComponents();

        header.add(title,
                createdAt,
                startsAt,
                owner);
        footer.add(members);

        container.add(header, description, footer);

        add(container);

    }
    private void createComponents(Conference conference) {
        System.out.println(conference);
        container = new VerticalLayout();
        header = new HorizontalLayout();
        title = new Span(conference.getTitle());
        createdAt = new Span(conference.getCreatedAt().toString());
        startsAt = new Span(conference.getStartsAt() == null ? "" : Duration.between(LocalDateTime.now(), conference.getStartsAt()).toHoursPart() + "hrs");
        owner = new Span(conference.getOwner() == null ? "" : conference.getOwner().getFirstName() + " " + conference.getOwner().getLastName());
        description = new Paragraph(conference.getDescription());
        footer = new HorizontalLayout();
        members = new Span(conference.getMember_limit() == null || conference.getMember_limit() <= 0 ? "Unlimited"
                : userConferenceService.findMembers(conference).size() + "/" + conference.getMember_limit());
    }

    private void setupComponents() {
        addClickListener(event -> authenticatedUser.get().ifPresent(user -> {
            if (conference.getOwner() != null && conference.getOwner().getUsername().equals(user.getUsername())) {
                new ConferenceDialog(userConferenceService,
                        conferenceService,
                        userService,
                        conference).open();
            }
        }));

        container.setSpacing(false);

        header.addClassNames(LumoUtility.Flex.AUTO,
                LumoUtility.FlexDirection.ROW,
                LumoUtility.JustifyContent.EVENLY,
                LumoUtility.AlignItems.CENTER,
                LumoUtility.Width.FULL);

        title.addClassNames(LumoUtility.FontSize.LARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.TextTransform.CAPITALIZE);

        createdAt.getStyle().set("font-style", "italic");
        createdAt.addClassNames(LumoUtility.FontWeight.THIN);

        startsAt.getStyle().set("font-style", "italic");
        startsAt.addClassNames(LumoUtility.FontWeight.THIN);

        description.getStyle().set("white-space", "normal"); // Allow text to wrap
        description.addClassNames(LumoUtility.Padding.Horizontal.XLARGE, LumoUtility.Margin.Horizontal.SMALL);

        footer.addClassNames(LumoUtility.Flex.AUTO,
                LumoUtility.Width.FULL,
                LumoUtility.FlexDirection.ROW_REVERSE);
    }

}

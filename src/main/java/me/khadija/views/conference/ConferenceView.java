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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
        startsAt = new Span();
        owner = new Span(conference.getOwner() == null ? "" : conference.getOwner().getFirstName() + " " + conference.getOwner().getLastName());
        description = new Paragraph(conference.getDescription());
        footer = new HorizontalLayout();
        members = new Span(conference.getMember_limit() == null || conference.getMember_limit() <= 0 ? "Unlimited"
                : userConferenceService.findMembers(conference).size() + "/" + conference.getMember_limit());

        if (conference.getStartsAt() != null) {
            final long diff = ChronoUnit.NANOS.between(conference.getStartsAt(), LocalDateTime.now());

            System.out.println("DIFF: " + -diff);
            System.out.println(getReadableTime(-diff));

            if (diff >= 0)
                startsAt.setText("Started");
            else
                startsAt.setText(getReadableTime(-diff));
        }

    }

    private String getReadableTime(Long nanos) {

        long tempSec = nanos / (1000 * 1000 * 1000);
        long sec = tempSec % 60;
        long min = (tempSec / 60) % 60;
        long hour = (tempSec / (60 * 60)) % 24;
        long day = (tempSec / (24 * 60 * 60)) % 24;

        if (day <= 0 && hour <= 0 && min <= 0)
            return String.format("%d seconds", sec);
        if (day <= 0 && hour <= 0)
            return String.format("%d minutes %d seconds", min, sec);
        if (day <= 0)
            return String.format("%d hours %d minutes %d seconds", hour, min, sec);

        return String.format("%d days %d hours %d minutes %d seconds", day, hour, min, sec);
    }

    private void setupComponents() {
        addClickListener(event -> authenticatedUser.get().ifPresent(user -> {
            if (conference.getOwner() != null && conference.getOwner().getUsername().equals(user.getUsername())) {
                new ConferenceDialog(userConferenceService,
                        conferenceService,
                        userService,
                        conference, false).open();
            } else {
                new ConferenceJoinDialog(userConferenceService,
                        conference,
                        authenticatedUser).open();
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

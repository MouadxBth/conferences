package me.khadija.views.dashboard;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import me.khadija.models.Conference;
import me.khadija.security.AuthenticatedUser;
import me.khadija.services.ConferenceService;
import me.khadija.services.UserConferenceService;
import me.khadija.services.UserService;
import me.khadija.views.MainLayout;
import me.khadija.views.conference.ConferenceDialog;
import me.khadija.views.conference.ConferenceListView;
import me.khadija.views.conference.ConferenceView;

import java.time.LocalDateTime;

@PageTitle("Dashboard")
@Route(value = "dashboard", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class DashboardView extends VerticalLayout implements AfterNavigationObserver {

    private final AuthenticatedUser authenticatedUser;
    private final UserConferenceService userConferenceService;
    private final UserService userService;
    private final ConferenceService conferenceService;

    private HorizontalLayout layout;
    private Button createConferenceButton;
    private ConferenceListView conferenceListView;


    public DashboardView(AuthenticatedUser authenticatedUser,
                         UserConferenceService userConferenceService,
                         UserService userService,
                         ConferenceService conferenceService) {
        this.userService = userService;
        this.conferenceService = conferenceService;
        this.userConferenceService = userConferenceService;
        this.authenticatedUser = authenticatedUser;

        setSizeFull();

        addClassNames(LumoUtility.Flex.AUTO,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Height.FULL);

        createComponents();
        setupComponents();

        layout.add(createConferenceButton);
        add(layout);

        add(conferenceListView);
    }

    private void createComponents() {
        layout = new HorizontalLayout();
        createConferenceButton = new Button("Create conference");
        conferenceListView = new ConferenceListView(userConferenceService, userService, conferenceService, authenticatedUser);
    }

    private void setupComponents() {
        layout.addClassNames(LumoUtility.Width.FULL,
                LumoUtility.Flex.AUTO,
                LumoUtility.FlexDirection.ROW,
                LumoUtility.AlignItems.CENTER,
                LumoUtility.JustifyContent.CENTER);
        layout.setMaxHeight("25px");

        createConferenceButton.addClickListener(event -> {
            final Conference conference = new Conference();
            authenticatedUser.get().ifPresent(conference::setOwner);
            conference.setCreatedAt(LocalDateTime.now());
            final ConferenceDialog conferenceDialog =
                    new ConferenceDialog(userConferenceService,
                            conferenceService,
                            userService, conference);
            conferenceDialog.setHeaderTitle("Add new Conference");
            conferenceDialog.open();
        });
    }


    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        conferenceListView.setItems(conferenceService.fetchAll()
                .stream()
                .filter(conference -> !conference.getPrivateConference())
                .toList());
    }


}

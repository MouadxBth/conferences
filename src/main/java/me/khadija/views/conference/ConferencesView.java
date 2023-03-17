package me.khadija.views.conference;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import me.khadija.security.AuthenticatedUser;
import me.khadija.services.ConferenceService;
import me.khadija.services.UserConferenceService;
import me.khadija.services.UserService;
import me.khadija.views.MainLayout;

@PageTitle("Conferences")
@Route(value = "conferences", layout = MainLayout.class)
@PermitAll
public class ConferencesView extends VerticalLayout implements AfterNavigationObserver {

    private final UserConferenceService userConferenceService;
    private final UserService userService;
    private final ConferenceService conferenceService;
    private final AuthenticatedUser authenticatedUser;
    private ConferenceListView conferenceListView;

    public ConferencesView(UserConferenceService userConferenceService,
                           UserService userService,
                           ConferenceService conferenceService, AuthenticatedUser authenticatedUser) {
        this.userConferenceService = userConferenceService;
        this.userService = userService;
        this.conferenceService = conferenceService;
        this.authenticatedUser = authenticatedUser;

        addClassNames(LumoUtility.Flex.AUTO,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Height.FULL);

        createComponents();

        add(conferenceListView);
    }

    private void createComponents() {
        conferenceListView = new ConferenceListView(userConferenceService,
                userService,
                conferenceService, authenticatedUser);
    }


    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        authenticatedUser.get().ifPresent(user ->
                conferenceListView.setItems(conferenceService.fetchAll()
                .stream()
                .filter(conference -> (conference.getOwner() != null
                        && conference.getOwner().getUsername().equals(user.getUsername()))
                || userConferenceService.isInConference(user, conference))
                .toList()));
    }

}

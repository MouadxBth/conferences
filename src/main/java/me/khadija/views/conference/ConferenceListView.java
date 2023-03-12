package me.khadija.views.conference;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import me.khadija.models.Conference;
import me.khadija.security.AuthenticatedUser;
import me.khadija.services.ConferenceService;
import me.khadija.services.UserConferenceService;
import me.khadija.services.UserService;

public class ConferenceListView extends Grid<Conference> {

    public ConferenceListView(UserConferenceService userConferenceService,
                              UserService userService,
                              ConferenceService conferenceService,
                              AuthenticatedUser authenticatedUser) {
        addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        addComponentColumn(conference -> new ConferenceView(conference,
                userConferenceService,
                userService,
                conferenceService, authenticatedUser));
    }
}

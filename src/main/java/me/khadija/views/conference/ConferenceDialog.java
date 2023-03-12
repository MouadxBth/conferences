package me.khadija.views.conference;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.binder.ValidationException;
import me.khadija.models.Conference;
import me.khadija.services.ConferenceService;
import me.khadija.services.UserConferenceService;
import me.khadija.services.UserService;
import me.khadija.views.dashboard.DashboardView;

import static me.khadija.utilities.Utilities.createNotification;

public class ConferenceDialog extends Dialog {

    private final Button saveButton = new Button("Save"),
            cancelButton = new Button("Cancel"),
            deleteButton = new Button("Delete");

    private final ConferenceService conferenceService;
    private final ConferenceForm conferenceForm;
    private final UserConferenceService userConferenceService;

    public ConferenceDialog(UserConferenceService userConferenceService,
                            ConferenceService conferenceService,
                            UserService userService,
                            Conference conference) {
        this.userConferenceService = userConferenceService;
        this.conferenceService = conferenceService;
        this.conferenceForm = new ConferenceForm(userService);

        setupForm(conference);
        setupButtons(conference);

        add(conferenceForm);

        getFooter().add(cancelButton,
                deleteButton,
                saveButton);
    }

    private void setupForm(Conference conference) {
        setHeaderTitle("Edit " + conference.getTitle());
        conferenceForm.getBinder().readBean(conference);

        conferenceForm.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new FormLayout.ResponsiveStep("500px", 3));
        conferenceForm.setColspan(conferenceForm.getDescription(), 2);
    }

    private void setupButtons(Conference conference) {
        cancelButton.addClickListener($ -> close());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickShortcut(Key.ENTER);

        saveButton.addClickListener(listener -> {
            try {
                conferenceForm.getBinder().writeBean(conference);
                if (conference.getId() == null) {
                    try {
                        userConferenceService.addConference(conference);
                    }
                    catch (Exception e) {
                        close();
                        final Notification notification = createNotification("Error: " + e.getMessage());
                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                        notification.setDuration(3 * 1000);
                        notification.open();
                        return;
                    }
                }
                else {
                    conferenceService.find(conference.getId())
                            .ifPresent(old -> conferenceService.update(conference));
                }

                final Notification notification = createNotification("Modified successfully!");
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setDuration(3 * 1000);
                notification.open();
                close();
                UI.getCurrent().navigate(DashboardView.class);
            } catch (ValidationException e) {
                final Notification notification = createNotification("No modification was applied!");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setDuration(3 * 1000);
                notification.open();
                throw new RuntimeException(e);
            }
        });

        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addClickShortcut(Key.ESCAPE);
        deleteButton.addClickListener(listener -> {
            try {
                conferenceForm.getBinder().writeBean(conference);
                conferenceService.delete(conference.getName());
                final Notification notification = createNotification("Deleted successfully!");
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setDuration(3 * 1000);
                notification.open();
                close();
                UI.getCurrent().navigate(DashboardView.class);
            } catch (ValidationException e) {
                final Notification notification = createNotification("No modification was applied!");
                notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
                notification.setDuration(3 * 1000);
                notification.open();
                throw new RuntimeException(e);
            }
        });
    }


}

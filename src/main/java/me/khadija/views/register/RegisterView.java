package me.khadija.views.register;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import me.khadija.registration.RegistrationRequest;
import me.khadija.security.AuthenticatedUser;
import me.khadija.services.RegistrationService;
import me.khadija.views.AuthLayout;
import me.khadija.views.dashboard.DashboardView;
import me.khadija.views.login.LoginView;

import static me.khadija.utilities.Utilities.createNotification;


@PageTitle("Register")
@Route(value = "register", layout = AuthLayout.class)
@AnonymousAllowed
@Uses(Icon.class)
public class RegisterView extends VerticalLayout implements BeforeEnterObserver {
    private final TextField firstName = new TextField("First name"),
            lastName = new TextField("Last name"),
            username = new TextField("Username");
    private final EmailField email = new EmailField("Email address");
    private final PasswordField passwordField = new PasswordField("Password"),
            passwordConfirmField = new PasswordField("Confirm password");
    private final Button cancel = new Button("Cancel"),
            save = new Button("Register");

    private final AuthenticatedUser authenticatedUser;

    public RegisterView(AuthenticatedUser authenticatedUser,
                        RegistrationService registrationService) {
        this.authenticatedUser = authenticatedUser;
        addClassName("register-view");

        addClassNames(LumoUtility.JustifyContent.CENTER,
                LumoUtility.AlignItems.CENTER);

        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());

        clearForm();
        cancel.addClickListener(e -> clearForm());

        save.addClickListener(e -> {
            if (username.getValue() == null || username.getValue().isBlank()
                    || email.getValue() == null || email.getValue().isBlank()
                    || passwordField.getValue() == null || passwordField.getValue().isBlank()
                    || passwordConfirmField.getValue() == null || passwordConfirmField.getValue().isBlank())
            {
                final Notification notification = createNotification("Please enter some valid credentials");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setDuration(3 * 1000);
                notification.open();
                return;
            }
                if (!passwordField.getValue().equals(passwordConfirmField.getValue())) {
                    final Notification notification = createNotification("Password confirmation doesnt match");
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.setDuration(3 * 1000);
                    notification.open();
                    return;
                }
            final RegistrationRequest request = new RegistrationRequest(
                    firstName.getValue(),
                    lastName.getValue(),
                    username.getValue(),
                    passwordField.getValue(),
                    email.getValue()
            );
            if (!registrationService.register(request)) {
                final Notification notification = createNotification("User already signed up!");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setDuration(3 * 1000);
                notification.open();
                return;
            }
            clearForm();
            save.getUI().ifPresent(ui -> ui.navigate(LoginView.class));
            final Notification notification = createNotification("Registered successfully! Confirm your email to login");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setDuration(3 * 1000);
            notification.open();
        });
    }

    private void clearForm() {
        firstName.clear();
        lastName.clear();
        username.clear();
        email.clear();
        passwordField.clear();
        passwordConfirmField.clear();
    }

    private Component createTitle() {
        return new H3("Register");
    }

    private Component createFormLayout() {
        final FormLayout formLayout = new FormLayout();

        formLayout.addClassNames(LumoUtility.Margin.Horizontal.AUTO);

        formLayout.setMaxWidth("760px");
        username.setRequired(true);
        passwordField.setRequired(true);
        passwordConfirmField.setRequired(true);
        email.setRequired(true);
        email.setErrorMessage("Please enter a valid email address");

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));

        formLayout.add(username, email,
                firstName, lastName,
                passwordField, passwordConfirmField);
        return formLayout;
    }

    private Component createButtonLayout() {
        final HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);

        buttonLayout.add(cancel);
        buttonLayout.add(save);

        return buttonLayout;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            event.forwardTo(DashboardView.class);
        }
    }
}

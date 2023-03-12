package me.khadija.views.login;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import me.khadija.security.AuthenticatedUser;
import me.khadija.services.RegistrationService;
import me.khadija.services.UserService;
import me.khadija.views.AuthLayout;
import me.khadija.views.dashboard.DashboardView;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.function.Supplier;

import static me.khadija.utilities.Utilities.createNotification;

@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login", layout = AuthLayout.class)
public class LoginView extends VerticalLayout implements BeforeEnterObserver, HasUrlParameter<String> {

    private final RegistrationService registrationService;
    private final AuthenticatedUser authenticatedUser;
    private final LoginForm loginForm = new LoginForm();


    public LoginView(RegistrationService registrationService,
                     AuthenticatedUser authenticatedUser) {
        this.registrationService = registrationService;
        this.authenticatedUser = authenticatedUser;

        addClassNames(LumoUtility.JustifyContent.CENTER,
                LumoUtility.AlignItems.CENTER);

        loginForm.setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));
        loginForm.setForgotPasswordButtonVisible(false);
        add(loginForm);


        //RouteUtil.getRoutePath(VaadinService.getCurrent().getContext()
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            event.forwardTo(DashboardView.class);
            return;
        }

        loginForm.setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent,
                             @OptionalParameter String param) {
        if (param != null) {
            if (!registrationService.confirm(param)) {
                final Notification notification = createNotification("Invalid confirmation token");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setDuration(3 * 1000);
                notification.open();
            }
            else {
                final Notification notification = createNotification("Email confirmed successfully!");
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setDuration(3 * 1000);
                notification.open();
            }
        }
    }
}

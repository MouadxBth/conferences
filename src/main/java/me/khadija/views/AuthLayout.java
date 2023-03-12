package me.khadija.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;
import me.khadija.views.login.LoginView;
import me.khadija.views.register.RegisterView;

public class AuthLayout extends AppLayout {

    public AuthLayout() {
        addToNavbar(createHeaderContent());
    }

    private Header createHeaderContent() {
        final Header header = new Header();
        header.addClassNames(LumoUtility.BoxSizing.BORDER,
                LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.ROW,
                LumoUtility.AlignItems.CENTER,
                LumoUtility.JustifyContent.BETWEEN,
                LumoUtility.Width.FULL,
                LumoUtility.Padding.Horizontal.MEDIUM);

        //System.out.println(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));

        final H1 title = new H1("Conferences");
        title.addClassNames(LumoUtility.FontSize.LARGE);

        final Button button = new Button();

        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        UI.getCurrent().getPage().fetchCurrentURL(url -> {
            if (url.getPath().contains("login")) {
                button.setText("Register");
            }
            else if (url.getPath().contains("register")) {
                button.setText("Login");
            }
        });

        button.addClickListener(e -> button.getUI().ifPresent(ui -> {

            UI.getCurrent().getPage().fetchCurrentURL(url -> {
                System.out.println(url);
                if (url.getPath().contains("login")) {
                    ui.navigate(RegisterView.class);
                    button.setText("Login");

                }
                else if (url.getPath().contains("register")) {
                    ui.navigate(LoginView.class);
                    button.setText("Register");
                }
            });
        }));


        header.add(title, button);
        return header;
    }
}

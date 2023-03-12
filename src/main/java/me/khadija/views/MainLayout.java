package me.khadija.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import me.khadija.models.Conference;
import me.khadija.models.User;
import me.khadija.models.UserConference;
import me.khadija.security.AuthenticatedUser;
import me.khadija.services.ConferenceService;
import me.khadija.services.UserConferenceService;
import me.khadija.services.UserService;
import me.khadija.views.conference.ChatView;
import me.khadija.views.conference.ConferenceDialog;
import me.khadija.views.conference.ConferencesView;
import me.khadija.views.dashboard.DashboardView;
import me.khadija.views.login.LoginView;
import me.khadija.views.register.RegisterView;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout implements BeforeEnterObserver {

    /**
     * A simple navigation item component, based on ListItem element.
     */
    public static class MenuItemInfo extends ListItem {

        private final Class<? extends Component> view;

        public MenuItemInfo(String menuTitle, Component icon, Class<? extends Component> view) {
            this.view = view;
            RouterLink link = new RouterLink();
            // Use Lumo classnames for various styling
            link.addClassNames(Display.FLEX, Gap.XSMALL, Height.MEDIUM, AlignItems.CENTER, Padding.Horizontal.SMALL,
                    TextColor.BODY);
            link.setRoute(view);

            Span text = new Span(menuTitle);
            // Use Lumo classnames for various styling
            text.addClassNames(FontWeight.MEDIUM, FontSize.MEDIUM, Whitespace.NOWRAP);

            if (icon != null) {
                link.add(icon);
            }
            link.add(text);
            add(link);
        }

        public Class<?> getView() {
            return view;
        }

    }

    private final AuthenticatedUser authenticatedUser;
    private final AccessAnnotationChecker accessChecker;

    public MainLayout(AuthenticatedUser authenticatedUser,
                      AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        addToNavbar(createHeaderContent());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (authenticatedUser.get().isEmpty()) {
            getUI().ifPresent(ui -> ui.navigate(LoginView.class));
        }
    }

    private Component createHeaderContent() {
        final Header header = new Header();
        header.addClassNames(BoxSizing.BORDER,
                Display.FLEX,
                FlexDirection.ROW,
                Width.FULL,
                Padding.Horizontal.MEDIUM,
                JustifyContent.EVENLY,
                AlignItems.CENTER);

        final H1 title = new H1("Conferences");
        title.addClassName(FontSize.LARGE);
        header.add(title);

        final Nav nav = new Nav();
        nav.addClassNames(Display.FLEX,
                Overflow.AUTO,
                Padding.Horizontal.MEDIUM,
                Padding.Vertical.XSMALL);

        // Wrap the links in a list; improves accessibility
        final UnorderedList list = new UnorderedList();
        list.addClassNames(Display.FLEX,
                Gap.SMALL,
                ListStyleType.NONE,
                Margin.NONE,
                Padding.NONE);
        nav.add(list);

        for (MenuItemInfo menuItem : createMenuItems()) {
            if (accessChecker.hasAccess(menuItem.getView())) {
                list.add(menuItem);
            }

        }

        header.add(nav);


        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            Avatar avatar = new Avatar(user.getFirstName() + " " + user.getLastName());
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getFirstName());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);
            userName.getSubMenu().addItem("Sign out", e -> {
                authenticatedUser.logout();
            });

            header.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            header.add(loginLink);
        }


        return header;
    }

    private MenuItemInfo[] createMenuItems() {
        return new MenuItemInfo[]{ //
                new MenuItemInfo("Dashboard", LineAwesomeIcon.LIST_SOLID.create(), DashboardView.class),
                new MenuItemInfo("My Conferences", LineAwesomeIcon.LIST_SOLID.create(), ConferencesView.class),
               // new MenuItemInfo("Chat", LineAwesomeIcon.COMMENTS.create(), ChatView.class)

        };
    }

}

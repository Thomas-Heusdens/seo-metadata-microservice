package be.thomasheusdens.seo_metadata_microservice.gui;

import be.thomasheusdens.seo_metadata_microservice.gui.components.LogoutButton;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {

    public MainLayout() {

        DrawerToggle toggle = new DrawerToggle();
        toggle.getStyle()
                .set("color", "var(--lumo-contrast-90pct)")
                .set("cursor", "pointer");

        H1 logo = new H1("SeoAnalyser");
        logo.getStyle()
                .set("margin", "0")
                .set("font-size", "1.5rem")
                .set("font-weight", "700")
                .set("padding", "0.5rem 1rem")
                .set("border-radius", "20px")
                .set("-webkit-text-fill-color", "black")
                .set("letter-spacing", "-0.5px");

        LogoutButton logoutButton = new LogoutButton();
        logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        logoutButton.setIcon(VaadinIcon.SIGN_OUT.create());
        logoutButton.getStyle()
                .set("color", "var(--lumo-error-color)")
                .set("font-weight", "500");

        Button loginButton = new Button("Login", e -> getUI().ifPresent(ui -> ui.navigate("login")));
        loginButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_CONTRAST);
        loginButton.getStyle()
                .set("font-weight", "500")
                .set("margin-right", "0.5rem");

        Button registerButton = new Button("Register", e -> getUI().ifPresent(ui -> ui.navigate("register")));
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerButton.getStyle()
                .set("font-weight", "500")
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("border", "none")
                .set("box-shadow", "0 4px 15px rgba(102, 126, 234, 0.4)")
                .set("transition", "all 0.3s ease");

        Span usernameDisplay = new Span();
        usernameDisplay.getStyle()
                .set("marginRight", "1rem")
                .set("font-weight", "500")
                .set("color", "var(--lumo-contrast-80pct)")
                .set("padding", "0.5rem 1rem")
                .set("background", "var(--lumo-contrast-5pct)")
                .set("border-radius", "20px")
                .set("font-size", "0.875rem");

        // Enhanced navbar styling
        getElement().getStyle()
                .set("box-shadow", "0 2px 10px rgba(0, 0, 0, 0.08)")
                .set("background", "var(--lumo-base-color)")
                .set("border-bottom", "1px solid var(--lumo-contrast-10pct)");

        addToNavbar(toggle, logo, usernameDisplay, logoutButton, loginButton, registerButton);

        // ADDED: Navigation drawer with home link
        createDrawerContent();

        // Add spacing for navbar items
        getElement().executeJs("""
            const navbar = this.querySelector('vaadin-app-layout')?.shadowRoot?.querySelector('[slot="navbar"]') || 
                          this.shadowRoot?.querySelector('[slot="navbar"]');
            if (navbar) {
                navbar.style.padding = '0.75rem 1.5rem';
                navbar.style.gap = '1rem';
                navbar.style.display = 'flex';
                navbar.style.alignItems = 'center';
            }
        """);

        // FIX: also pass logoutButton
        getElement().executeJs("""
            (function(userSpan, loginBtn, registerBtn, logoutBtn) {
                const token = sessionStorage.getItem("accessToken");

                if (!token) {
                    // NOT LOGGED IN
                    userSpan.textContent = "";
                    loginBtn.style.display = "inline-block";
                    registerBtn.style.display = "inline-block";
                    logoutBtn.style.display = "none";
                    return;
                }

                try {
                    const payload = JSON.parse(atob(token.split('.')[1]));
                    const username = payload.sub || payload.username || "";

                    if (username) {
                        userSpan.textContent = "Logged in as: " + username;
                    }

                    // Hide login/register, show logout
                    loginBtn.style.display = "none";
                    registerBtn.style.display = "none";
                    logoutBtn.style.display = "inline-block";

                } catch (e) {
                    console.error("JWT decode error", e);
                }
            })($0, $1, $2, $3);
        """,
                usernameDisplay.getElement(),
                loginButton.getElement(),
                registerButton.getElement(),
                logoutButton.getElement()
        );
    }

    /**
     * ADDED: Creates the navigation drawer with links to accessible pages
     */
    private void createDrawerContent() {
        // Create home link
        RouterLink homeLink = new RouterLink("Home", HomeView.class);
        homeLink.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("padding", "0.75rem 1rem")
                .set("text-decoration", "none")
                .set("color", "var(--lumo-contrast-90pct)")
                .set("border-radius", "8px")
                .set("transition", "all 0.2s ease")
                .set("font-weight", "500")
                .set("margin", "0.25rem 0");

        // Add icon to home link
        homeLink.getElement().insertChild(0, VaadinIcon.HOME.create().getElement());
        homeLink.getElement().getChild(0).getStyle()
                .set("margin-right", "0.75rem")
                .set("color", "#667eea");

        // Hover effect
        homeLink.getElement().executeJs("""
            this.addEventListener('mouseenter', () => {
                this.style.background = 'var(--lumo-contrast-5pct)';
                this.style.transform = 'translateX(4px)';
            });
            this.addEventListener('mouseleave', () => {
                this.style.background = 'transparent';
                this.style.transform = 'translateX(0)';
            });
        """);

        // Style the drawer
        addToDrawer(homeLink);

        // Additional drawer styling
        getElement().executeJs("""
            const drawer = this.shadowRoot?.querySelector('[part="drawer"]');
            if (drawer) {
                drawer.style.padding = '1rem';
                drawer.style.background = 'var(--lumo-base-color)';
            }
        """);
    }
}
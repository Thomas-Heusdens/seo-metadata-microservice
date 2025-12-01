package be.thomasheusdens.seo_metadata_microservice.gui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "login")
@PageTitle("Login")
@AnonymousAllowed
public class LoginView extends Main implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();

    public LoginView() {
        addClassName("login-view");

        login.setForgotPasswordButtonVisible(false);
        login.setAction(null);

        // Disable browser autocomplete
        login.getElement().setAttribute("autocomplete", "off");
        login.getElement().getStyle().set("autocomplete", "off");
        login.getChildren().forEach(child -> child.getElement().setAttribute("autocomplete", "off"));

        // Style the login form
        login.getStyle()
                .set("padding", "2rem")
                .set("background", "var(--lumo-base-color)")
                .set("border-radius", "16px")
                .set("box-shadow", "0 10px 40px rgba(0, 0, 0, 0.1)")
                .set("width", "100%")
                .set("max-width", "400px");

        // ---- Normal username/password login (already there) ----
        login.addLoginListener(e -> {
            getElement().executeJs("""
                fetch('/api/auth/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    credentials: 'include',
                    body: JSON.stringify({
                        username: $0,
                        password: $1
                    })
                })
                .then(res => {
                    if (!res.ok) {
                        $2.setError(true);
                        return null;
                    }
                    return res.json();
                })
                .then(data => {
                    if (!data) return;
                    sessionStorage.setItem("accessToken", data.accessToken);
                    window.location.href = "/";
                })
                .catch(err => console.error("Login failed", err));
            """, e.getUsername(), e.getPassword(), login);
        });

        // Header
        H2 title = new H2("Welcome Back");
        title.getStyle()
                .set("margin", "0 0 0.5rem 0")
                .set("font-size", "1.75rem")
                .set("font-weight", "700")
                .set("color", "var(--lumo-contrast-90pct)")
                .set("text-align", "center");

        Span subtitle = new Span("Sign in to continue to SeoAnalyser");
        subtitle.getStyle()
                .set("color", "var(--lumo-contrast-60pct)")
                .set("font-size", "0.875rem")
                .set("display", "block")
                .set("text-align", "center")
                .set("margin-bottom", "2rem");

        Div header = new Div(title, subtitle);

        // Divider
        Div divider = new Div();
        divider.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("margin", "1.5rem 0")
                .set("color", "var(--lumo-contrast-50pct)")
                .set("font-size", "0.875rem");
        divider.getElement().setProperty("innerHTML",
                "<span style='flex: 1; height: 1px; background: var(--lumo-contrast-20pct);'></span>" +
                        "<span style='padding: 0 1rem;'>or continue with</span>" +
                        "<span style='flex: 1; height: 1px; background: var(--lumo-contrast-20pct);'></span>");

        // ---- OAuth buttons ----
        Button googleButton = new Button("Google", VaadinIcon.GOOGLE_PLUS.create(), click ->
                getUI().ifPresent(ui -> ui.getPage().setLocation("/oauth2/authorization/google"))
        );
        googleButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        googleButton.getStyle()
                .set("width", "100%")
                .set("justify-content", "center")
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("font-weight", "500")
                .set("padding", "0.75rem")
                .set("border-radius", "8px")
                .set("transition", "all 0.2s ease");

        Button githubButton = new Button("GitHub", VaadinIcon.CODE.create(), click ->
                getUI().ifPresent(ui -> ui.getPage().setLocation("/oauth2/authorization/github"))
        );
        githubButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        githubButton.getStyle()
                .set("width", "100%")
                .set("justify-content", "center")
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("font-weight", "500")
                .set("padding", "0.75rem")
                .set("border-radius", "8px")
                .set("transition", "all 0.2s ease");

        Div oauthButtons = new Div(googleButton, githubButton);
        oauthButtons.getStyle()
                .set("display", "flex")
                .set("flexDirection", "column")
                .set("gap", "0.75rem")
                .set("marginTop", "1rem");

        // Register link
        Span registerText = new Span("Don't have an account? ");
        Button registerLink = new Button("Sign up", e -> getUI().ifPresent(ui -> ui.navigate("register")));
        registerLink.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        registerLink.getStyle()
                .set("color", "#667eea")
                .set("font-weight", "600")
                .set("padding", "0")
                .set("min-width", "auto");

        Div registerDiv = new Div(registerText, registerLink);
        registerDiv.getStyle()
                .set("text-align", "center")
                .set("margin-top", "1.5rem")
                .set("color", "var(--lumo-contrast-70pct)")
                .set("font-size", "0.875rem");

        Div card = new Div(header, login, divider, oauthButtons, registerDiv);
        card.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border-radius", "16px")
                .set("padding", "2.5rem")
                .set("box-shadow", "0 20px 60px rgba(0, 0, 0, 0.12)")
                .set("width", "100%")
                .set("max-width", "440px")
                .set("animation", "slideUp 0.4s ease-out");

        Div container = new Div(card);
        container.getStyle()
                .set("display", "flex")
                .set("justify-content", "center")
                .set("align-items", "center")
                .set("min-height", "100vh")
                .set("padding", "2rem")
                .set("background", "linear-gradient(135deg, #667eea15 0%, #764ba215 100%)");

        setSizeFull();
        getStyle()
                .set("margin", "0")
                .set("padding", "0");

        add(container);

        // Add animation and remove LoginForm padding
        getElement().executeJs("""
            const style = document.createElement('style');
            style.textContent = `
                @keyframes slideUp {
                    from {
                        opacity: 0;
                        transform: translateY(20px);
                    }
                    to {
                        opacity: 1;
                        transform: translateY(0);
                    }
                }
            `;
            document.head.appendChild(style);
       \s""");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        event.getUI().getPage().executeJs("""
            if (sessionStorage.getItem("accessToken")) {
                window.location.href = "/";
            }
        """);
    }
}
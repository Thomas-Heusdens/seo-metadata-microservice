package be.thomasheusdens.seo_metadata_microservice.gui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("register")
@PageTitle("Register")
@AnonymousAllowed
public class RegisterView extends VerticalLayout implements BeforeEnterObserver {

    private final TextField usernameField = new TextField("Username");
    private final PasswordField passwordField = new PasswordField("Password");
    private final PasswordField confirmPasswordField = new PasswordField("Confirm Password");

    private final ProgressBar strengthBar = new ProgressBar();
    private final Div strengthLabel = new Div();

    private final Button registerButton = new Button("Register");

    public RegisterView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setPadding(false);
        setSpacing(false);
        getStyle()
                .set("background", "linear-gradient(135deg, #667eea15 0%, #764ba215 100%)")
                .set("min-height", "100vh");

        H2 title = new H2("Create Your Account");
        title.getStyle()
                .set("margin", "0 0 0.5rem 0")
                .set("font-size", "1.75rem")
                .set("font-weight", "700")
                .set("color", "var(--lumo-contrast-90pct)")
                .set("text-align", "center");

        Span subtitle = new Span("Join SeoAnalyser today");
        subtitle.getStyle()
                .set("color", "var(--lumo-contrast-60pct)")
                .set("font-size", "0.875rem")
                .set("display", "block")
                .set("text-align", "center")
                .set("margin-bottom", "2rem");

        Div header = new Div(title, subtitle);

        initPasswordStrengthMeter();

        usernameField.setRequired(true);
        usernameField.getStyle()
                .set("width", "100%");
        usernameField.setPrefixComponent(VaadinIcon.USER.create());

        passwordField.setRequired(true);
        passwordField.getStyle()
                .set("width", "100%");
        passwordField.setPrefixComponent(VaadinIcon.LOCK.create());

        confirmPasswordField.setRequired(true);
        confirmPasswordField.getStyle()
                .set("width", "100%");
        confirmPasswordField.setPrefixComponent(VaadinIcon.CHECK_CIRCLE.create());

        // Style strength meter
        strengthBar.getStyle()
                .set("width", "100%")
                .set("height", "6px")
                .set("margin-top", "0.5rem")
                .set("border-radius", "3px");

        strengthLabel.getStyle()
                .set("font-size", "0.813rem")
                .set("margin-top", "0.5rem")
                .set("font-weight", "500");

        Div strengthContainer = new Div(strengthBar, strengthLabel);
        strengthContainer.getStyle()
                .set("width", "100%")
                .set("margin-top", "-0.5rem");

        FormLayout form = new FormLayout();
        form.add(usernameField, passwordField, confirmPasswordField, strengthContainer);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        form.getStyle()
                .set("width", "100%")
                .set("gap", "1rem");

        registerButton.addClickListener(e -> registerUser());
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        registerButton.getStyle()
                .set("width", "100%")
                .set("margin-top", "1rem")
                .set("font-weight", "600")
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("border", "none")
                .set("box-shadow", "0 6px 20px rgba(102, 126, 234, 0.4)")
                .set("transition", "all 0.3s ease")
                .set("cursor", "pointer");

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
                        "<span style='padding: 0 1rem;'>or sign up with</span>" +
                        "<span style='flex: 1; height: 1px; background: var(--lumo-contrast-20pct);'></span>");

        Button googleButton = new Button("Google", VaadinIcon.GOOGLE_PLUS.create(),
                click -> getUI().ifPresent(ui -> ui.getPage().setLocation("/oauth2/authorization/google"))
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

        Button githubButton = new Button("GitHub", VaadinIcon.CODE.create(),
                click -> getUI().ifPresent(ui -> ui.getPage().setLocation("/oauth2/authorization/github"))
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
                .set("gap", "0.75rem");

        // Login link
        Span loginText = new Span("Already have an account? ");
        Button loginLink = new Button("Sign in", e -> getUI().ifPresent(ui -> ui.navigate("login")));
        loginLink.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        loginLink.getStyle()
                .set("color", "#667eea")
                .set("font-weight", "600")
                .set("padding", "0")
                .set("min-width", "auto");

        Div loginDiv = new Div(loginText, loginLink);
        loginDiv.getStyle()
                .set("text-align", "center")
                .set("margin-top", "1.5rem")
                .set("color", "var(--lumo-contrast-70pct)")
                .set("font-size", "0.875rem");

        Div content = new Div(header, form, registerButton, divider, oauthButtons, loginDiv);
        content.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border-radius", "16px")
                .set("padding", "2.5rem")
                .set("box-shadow", "0 20px 60px rgba(0, 0, 0, 0.12)")
                .set("width", "100%")
                .set("max-width", "440px")
                .set("margin", "2rem auto")
                .set("animation", "slideUp 0.4s ease-out");

        content.getElement().setAttribute("autocomplete", "off");
        content.getElement().getStyle().set("autocomplete", "off");
        content.getChildren().forEach(child -> child.getElement().setAttribute("autocomplete", "off"));

        add(content);

        // Apply saved theme on load + animation
        getElement().executeJs("""
            const saved = localStorage.getItem("theme");
            if (saved === "dark") {
                document.documentElement.setAttribute("theme", "dark");
            }
           \s
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

    // ------------------------------------
    // PASSWORD STRENGTH
    // ------------------------------------
    private void initPasswordStrengthMeter() {

        strengthBar.setMin(0);
        strengthBar.setMax(100);
        strengthBar.setValue(0);
        strengthLabel.setText("Password strength: ");

        passwordField.addValueChangeListener(e -> {
            String pwd = passwordField.getValue();
            int score = calculateStrength(pwd);

            strengthBar.setValue(score);

            if (score < 40) {
                strengthBar.getStyle().set("color", "#ef4444");
                strengthLabel.setText("Weak");
                strengthLabel.getStyle().set("color", "#ef4444");
            } else if (score < 80) {
                strengthBar.getStyle().set("color", "#f59e0b");
                strengthLabel.setText("Medium");
                strengthLabel.getStyle().set("color", "#f59e0b");
            } else {
                strengthBar.getStyle().set("color", "#10b981");
                strengthLabel.setText("Strong");
                strengthLabel.getStyle().set("color", "#10b981");
            }
        });
    }

    private int calculateStrength(String password) {
        int score = 0;

        if (password.length() > 6) score += 20;
        if (password.length() > 10) score += 20;
        if (password.matches(".*[A-Z].*")) score += 20;
        if (password.matches(".*[0-9].*")) score += 20;
        if (password.matches(".*[^a-zA-Z0-9].*")) score += 20;

        return score;
    }

    // ------------------------------------
    // REGISTER USER (JS version)
    // ------------------------------------
    private void registerUser() {
        String username = usernameField.getValue();
        String password = passwordField.getValue();
        String confirmPassword = confirmPasswordField.getValue();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Notification.show("All fields are required");
            return;
        }

        if (!password.equals(confirmPassword)) {
            Notification.show("Passwords do not match");
            return;
        }

        getElement().executeJs("""
            fetch('/api/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify({
                    username: $0,
                    password: $1
                })
            })
            .then(res => {
                if (res.status === 409) {
                    $2.$server.showNotification("Username already taken");
                    return null;
                }
                if (!res.ok) {
                    $2.$server.showNotification("Registration failed");
                    return null;
                }
                return res.json();
            })
            .then(data => {
                if (!data) return;

                // AUTO LOGIN
                fetch('/api/auth/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    credentials: 'include',
                    body: JSON.stringify({
                        username: $0,
                        password: $1
                    })
                })
                .then(r => r.json())
                .then(loginData => {
                    sessionStorage.setItem("accessToken", loginData.accessToken);
                    window.location.href = "/";
                });
            })
        """, username, password, getElement());
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
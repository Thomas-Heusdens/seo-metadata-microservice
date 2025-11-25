package be.thomasheusdens.seo_metadata_microservice.gui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
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

        H2 title = new H2("Create Your Account");

        initPasswordStrengthMeter();

        usernameField.setRequired(true);
        passwordField.setRequired(true);
        confirmPasswordField.setRequired(true);

        FormLayout form = new FormLayout();
        form.add(usernameField, passwordField, confirmPasswordField, strengthBar, strengthLabel);

        registerButton.addClickListener(e -> registerUser());

        Div content = new Div(title, form, registerButton);
        content.getStyle().set("maxWidth", "400px");
        content.getStyle().set("margin", "auto");
        content.getStyle().set("padding", "20px");

        content.getElement().setAttribute("autocomplete", "off");
        content.getElement().getStyle().set("autocomplete", "off");
        content.getChildren().forEach(child -> child.getElement().setAttribute("autocomplete", "off"));

        add(content);

        // Apply saved theme on load
        getElement().executeJs("""
            const saved = localStorage.getItem("theme");
            if (saved === "dark") {
                document.documentElement.setAttribute("theme", "dark");
            }
        """);
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
                strengthBar.getStyle().set("color", "red");
                strengthLabel.setText("Weak");
            } else if (score < 80) {
                strengthBar.getStyle().set("color", "orange");
                strengthLabel.setText("Medium");
            } else {
                strengthBar.getStyle().set("color", "green");
                strengthLabel.setText("Strong");
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
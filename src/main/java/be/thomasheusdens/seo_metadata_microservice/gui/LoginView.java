package be.thomasheusdens.seo_metadata_microservice.gui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.component.notification.Notification;
import elemental.json.Json;
import elemental.json.JsonObject;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Route(value = "login")
@PageTitle("Login")
@AnonymousAllowed
public class LoginView extends Main implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();

    public LoginView() {
        login.setForgotPasswordButtonVisible(false);
        login.setAction(null);

        login.getElement().setAttribute("autocomplete", "off");
        login.getElement().getStyle().set("autocomplete", "off");
        login.getChildren().forEach(child -> child.getElement().setAttribute("autocomplete", "off"));

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

        setSizeFull();
        add(new Div(login));
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

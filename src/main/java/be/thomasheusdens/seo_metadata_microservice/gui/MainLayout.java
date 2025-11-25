package be.thomasheusdens.seo_metadata_microservice.gui;

import be.thomasheusdens.seo_metadata_microservice.gui.components.LogoutButton;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {

    public MainLayout() {
        DrawerToggle toggle = new DrawerToggle();
        H1 logo = new H1("SeoAnalyser");

        LogoutButton logoutButton = new LogoutButton();

        Button loginButton = new Button("Login", e -> getUI().ifPresent(ui -> ui.navigate("login")));
        Button registerButton = new Button("Register", e -> getUI().ifPresent(ui -> ui.navigate("register")));

        addToNavbar(toggle, logo, logoutButton, loginButton, registerButton);

        // Hide login/register on client
        getElement().executeJs("""
            setTimeout(() => {
                const token = sessionStorage.getItem("accessToken");
                if (token) {
                    const host = $0;
        
                    host.querySelectorAll("button").forEach(btn => {
                        if (btn.textContent.includes("Login") || btn.textContent.includes("Register")) {
                            btn.style.display = "none";
                        }
                    });
                }
            }, 150);
        """, getElement());
    }
}
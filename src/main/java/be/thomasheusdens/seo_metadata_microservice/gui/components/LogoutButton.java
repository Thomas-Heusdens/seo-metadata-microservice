package be.thomasheusdens.seo_metadata_microservice.gui.components;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.button.Button;

public class LogoutButton extends Button {

    public LogoutButton() {
        super("Logout");

        addClickListener(e -> performLogout());
    }

    private void performLogout() {

        getElement().executeJs("""
            const token = sessionStorage.getItem('accessToken');

            if (!token) {
                $0.$server.showNotification("You are not logged in");
                return;
            }

            fetch('/api/auth/logout', {
                  method: 'POST',
                  credentials: 'include',
                  headers: {
                      'Content-Type': 'application/json'
                  },
                  body: JSON.stringify({ logoutAll: false })
              }).then(res => {
                sessionStorage.removeItem('accessToken');
                window.location.href = '/login';
            }).catch(err => {
                console.error("Logout failed", err);
                sessionStorage.removeItem('accessToken');
                window.location.href = '/login';
            });
        """, getElement());
    }

    @ClientCallable
    private void showNotification(String message) {
        com.vaadin.flow.component.notification.Notification.show(message);
    }
}
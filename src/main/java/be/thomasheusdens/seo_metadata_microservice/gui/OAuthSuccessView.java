package be.thomasheusdens.seo_metadata_microservice.gui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("oauth2/success")
@PageTitle("OAuth Login Success")
@AnonymousAllowed
public class OAuthSuccessView extends Div implements BeforeEnterObserver {

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

        String token = event.getLocation()
                .getQueryParameters()
                .getSingleParameter("token")
                .orElse("");

        getElement().executeJs("""
            if ($0) {
                sessionStorage.setItem("accessToken", $0);
            }
            window.location.href = "/";
        """, token);
    }
}

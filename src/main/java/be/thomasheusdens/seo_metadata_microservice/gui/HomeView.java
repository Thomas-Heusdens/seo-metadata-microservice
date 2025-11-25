package be.thomasheusdens.seo_metadata_microservice.gui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "", layout = MainLayout.class)
@PageTitle("SeoAnalyser")
@AnonymousAllowed
public class HomeView extends VerticalLayout {

    private final TextField urlField = new TextField("Enter URL to scan");
    private final Button scanButton = new Button("Scan");

    public HomeView() {
        setAlignItems(Alignment.CENTER);

        add(new H1("SeoAnalyser"), urlField, scanButton);

        scanButton.addClickListener(e -> handleScan());
    }

    private void handleScan() {
        getElement().executeJs("""
            const token = sessionStorage.getItem("accessToken");
            
            if (!token) {
                window.location.href = "/login";
                return;
            }

            const url = $0.value;
            if (!url) return;

            // Later redirect to analysis view
            window.location.href = "/analysis?url=" + encodeURIComponent(url);
        """, urlField.getElement());
    }
}
package be.thomasheusdens.seo_metadata_microservice.gui;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonType;
import elemental.json.JsonValue;

@Route(value = "analysis", layout = MainLayout.class)
@PageTitle("SEO Analysis")
@PreserveOnRefresh
public class SeoAnalysisView extends Composite<VerticalLayout> implements BeforeEnterObserver {

    private final Div container = new Div();
    private final Div statsContainer = new Div();

    public SeoAnalysisView() {
        getElement().getStyle()
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("height", "100%")
                .set("min-height", "100vh");

        VerticalLayout layout = getContent();
        layout.setSizeUndefined();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        // Remove the background from here (important!)
        layout.getStyle()
                .set("width", "100%")
                .set("min-height", "100%");

        // FIXED: Header section now centered
        Div header = new Div();
        header.getStyle()
                .set("padding", "2rem")
                .set("text-align", "center")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center");  // Added for centering

        H2 title = new H2("SEO Analysis Results");
        title.getStyle()
                .set("color", "white")
                .set("font-weight", "800")
                .set("font-size", "2.5rem")
                .set("text-align", "center")
                .set("margin", "0")
                .set("text-shadow", "0 2px 10px rgba(0,0,0,0.2)");

        header.add(title);

        // FIXED: Stats container now centered
        statsContainer.getStyle()
                .set("display", "flex")
                .set("gap", "1rem")
                .set("justify-content", "center")  // This centers the stats
                .set("flex-wrap", "wrap")
                .set("padding", "0 2rem 2rem 2rem")
                .set("align-items", "center")
                .set("margin", "0 auto");  // Added for extra centering

        // Main container
        container.getStyle()
                .set("max-width", "1200px")
                .set("margin", "0 auto")
                .set("padding", "2rem");

        layout.add(header, statsContainer, container);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String url = event.getLocation().getQueryParameters()
                .getParameters()
                .getOrDefault("url", java.util.List.of(""))
                .get(0);

        if (url.isBlank()) {
            showError("❌ No URL provided.");
            return;
        }

        callBackend(url);
    }

    private void callBackend(String url) {
        container.removeAll();
        container.add(createLoadingIndicator());

        getContent().getElement().executeJs("""
            const token = sessionStorage.getItem("accessToken");
            if (!token) { $0.$server.authError(); return; }

            fetch("/api/seo/analyze?url=" + encodeURIComponent($1), {
                method: "GET",
                headers: { "Authorization": "Bearer " + token }
            })
            .then(r => r.json())
            .then(json => $0.$server.showResults(json))
            .catch(err => $0.$server.error(err.toString()));
        """, getContent().getElement(), url);
    }

    @ClientCallable
    private void authError() {
        showError("❌ Authentication required. Please log in.");
    }

    @ClientCallable
    private void error(String err) {
        showError("❌ Error: " + err);
    }

    private void showError(String message) {
        container.removeAll();
        statsContainer.removeAll();

        Div errorCard = new Div();
        errorCard.getStyle()
                .set("background", "white")
                .set("padding", "2rem")
                .set("border-radius", "12px")
                .set("text-align", "center")
                .set("box-shadow", "0 4px 20px rgba(0,0,0,0.1)");

        errorCard.add(new Paragraph(message));
        container.add(errorCard);
    }

    @ClientCallable
    private void showResults(JsonObject json) {
        container.removeAll();
        statsContainer.removeAll();

        if (json == null || !json.hasKey("url")) {
            showError("❌ Invalid response format.");
            return;
        }

        // Show scanned URL
        String scanned = safeJsonString(json, "url");
        Div urlCard = createUrlCard(scanned);
        container.add(urlCard);

        // Get checks array
        if (!json.hasKey("checks") || json.get("checks") == null) {
            showError("❌ No checks in response.");
            return;
        }

        JsonArray checks = json.getArray("checks");

        // Calculate stats
        int okCount = 0;
        int warningCount = 0;
        int criticalCount = 0;

        for (int i = 0; i < checks.length(); i++) {
            JsonObject check = checks.getObject(i);
            String status = safeJsonString(check, "status");

            if ("OK".equalsIgnoreCase(status)) {
                okCount++;
            } else if ("WARNING".equalsIgnoreCase(status)) {
                warningCount++;
            } else if ("CRITICAL".equalsIgnoreCase(status)) {
                criticalCount++;
            }
        }

        // Show stats
        statsContainer.add(createStatCard("✅ Passed", String.valueOf(okCount), "#10b981"));
        statsContainer.add(createStatCard("⚠️ Warnings", String.valueOf(warningCount), "#f59e0b"));
        statsContainer.add(createStatCard("🚨 Critical", String.valueOf(criticalCount), "#ef4444"));

        // Create sections
        Div criticalSection = new Div();
        Div warningSection = new Div();
        Div okSection = new Div();

        boolean hasCritical = false;
        boolean hasWarning = false;
        boolean hasOk = false;

        // Sort checks into sections
        for (int i = 0; i < checks.length(); i++) {
            JsonObject check = checks.getObject(i);

            String label = safeJsonString(check, "label");
            String status = safeJsonString(check, "status");
            String message = safeJsonString(check, "message");
            String recommendation = safeJsonString(check, "recommendation");
            String snippet = safeJsonString(check, "snippet");

            Div card = createCheckCard(label, message, status, recommendation, snippet);

            if ("CRITICAL".equalsIgnoreCase(status)) {
                criticalSection.add(card);
                hasCritical = true;
            } else if ("WARNING".equalsIgnoreCase(status)) {
                warningSection.add(card);
                hasWarning = true;
            } else {
                okSection.add(card);
                hasOk = true;
            }
        }

        // Add sections with headers
        if (hasCritical) {
            container.add(createSectionHeader("🚨 Critical Issues", "#ef4444"));
            container.add(criticalSection);
        }

        if (hasWarning) {
            container.add(createSectionHeader("⚠️ Warnings", "#f59e0b"));
            container.add(warningSection);
        }

        if (hasOk) {
            container.add(createSectionHeader("✅ Passed Checks", "#10b981"));
            container.add(okSection);
        }
    }

    private Div createLoadingIndicator() {
        Div loading = new Div();
        loading.getStyle()
                .set("background", "white")
                .set("padding", "3rem")
                .set("border-radius", "12px")
                .set("text-align", "center")
                .set("box-shadow", "0 4px 20px rgba(0,0,0,0.1)");

        Paragraph text = new Paragraph("🔍 Analyzing your website...");
        text.getStyle()
                .set("font-size", "1.25rem")
                .set("color", "#667eea")
                .set("font-weight", "600");

        loading.add(text);
        return loading;
    }

    private Div createUrlCard(String url) {
        Div card = new Div();
        card.getStyle()
                .set("background", "white")
                .set("padding", "1.5rem")
                .set("border-radius", "12px")
                .set("margin-bottom", "2rem")
                .set("box-shadow", "0 4px 20px rgba(0,0,0,0.1)");

        Span label = new Span("🔍 Analyzed URL:");
        label.getStyle()
                .set("font-weight", "600")
                .set("color", "#64748b")
                .set("margin-right", "0.5rem");

        Span urlSpan = new Span(url);
        urlSpan.getStyle()
                .set("color", "#667eea")
                .set("word-break", "break-all");

        card.add(label, urlSpan);
        return card;
    }

    private Div createStatCard(String label, String value, String color) {
        Div card = new Div();
        card.getStyle()
                .set("background", "white")
                .set("padding", "1.5rem")
                .set("border-radius", "12px")
                .set("text-align", "center")
                .set("min-width", "150px")
                .set("box-shadow", "0 4px 20px rgba(0,0,0,0.1)");

        Span valueSpan = new Span(value);
        valueSpan.getStyle()
                .set("display", "block")
                .set("font-size", "2rem")
                .set("font-weight", "800")
                .set("color", color)
                .set("margin-bottom", "0.5rem");

        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                .set("display", "block")
                .set("color", "#64748b")
                .set("font-size", "0.875rem")
                .set("font-weight", "600");

        card.add(valueSpan, labelSpan);
        return card;
    }

    private H3 createSectionHeader(String text, String color) {
        H3 header = new H3(text);
        header.getStyle()
                .set("color", "white")
                .set("font-weight", "700")
                .set("font-size", "1.5rem")
                .set("margin", "2rem 0 1rem 0")
                .set("padding-bottom", "0.5rem")
                .set("border-bottom", "3px solid " + color);
        return header;
    }

    private Div createCheckCard(String label, String message, String status, String recommendation, String snippet) {
        boolean isCritical = "CRITICAL".equalsIgnoreCase(status);
        boolean isWarning = "WARNING".equalsIgnoreCase(status);
        boolean isOk = "OK".equalsIgnoreCase(status);

        String bgColor = isOk ? "#f0fdf4" : (isWarning ? "#fffbeb" : "#fef2f2");
        String borderColor = isOk ? "#10b981" : (isWarning ? "#f59e0b" : "#ef4444");
        String iconColor = isOk ? "#10b981" : (isWarning ? "#f59e0b" : "#ef4444");

        Div card = new Div();
        card.getStyle()
                .set("background", bgColor)
                .set("border-left", "4px solid " + borderColor)
                .set("padding", "1.5rem")
                .set("border-radius", "8px")
                .set("margin-bottom", "1rem")
                .set("cursor", "pointer")
                .set("transition", "all 0.2s ease")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.05)");

        // Header
        HorizontalLayout header = new HorizontalLayout();
        header.setSpacing(true);
        header.setAlignItems(HorizontalLayout.Alignment.CENTER);
        header.getStyle().set("width", "100%");

        Span icon = new Span(isOk ? "✅" : (isWarning ? "⚠️" : "🚨"));
        icon.getStyle()
                .set("font-size", "1.5rem")
                .set("margin-right", "0.5rem");

        Span titleSpan = new Span(label);
        titleSpan.getStyle()
                .set("font-weight", "700")
                .set("font-size", "1.125rem")
                .set("color", "#1e293b")
                .set("flex", "1");

        header.add(icon, titleSpan);

        Paragraph details = new Paragraph(message);
        details.getStyle()
                .set("margin", "0.5rem 0 0 2.5rem")
                .set("color", "#475569")
                .set("line-height", "1.5");

        Div expandable = new Div();
        expandable.setVisible(false);
        expandable.getStyle()
                .set("margin-top", "1rem")
                .set("padding-top", "1rem")
                .set("border-top", "1px solid rgba(0,0,0,0.1)");

        if (recommendation != null && !recommendation.isEmpty()) {
            Div recDiv = new Div();
            recDiv.getStyle()
                    .set("margin-bottom", "1rem")
                    .set("padding", "1rem")
                    .set("background", "white")
                    .set("border-radius", "6px");

            Span recLabel = new Span("📌 Recommendation");
            recLabel.getStyle()
                    .set("display", "block")
                    .set("font-weight", "600")
                    .set("color", "#1e293b")
                    .set("margin-bottom", "0.5rem");

            Paragraph recText = new Paragraph(recommendation);
            recText.getStyle()
                    .set("margin", "0")
                    .set("color", "#475569");

            recDiv.add(recLabel, recText);
            expandable.add(recDiv);
        }

        if (snippet != null && !snippet.isEmpty()) {
            Div snippetDiv = new Div();
            snippetDiv.getStyle()
                    .set("padding", "1rem")
                    .set("background", "#1e293b")
                    .set("border-radius", "6px")
                    .set("overflow-x", "auto");

            Span snippetLabel = new Span("💻 Example Code");
            snippetLabel.getStyle()
                    .set("display", "block")
                    .set("font-weight", "600")
                    .set("color", "white")
                    .set("margin-bottom", "0.5rem");

            Paragraph snippetText = new Paragraph(snippet);
            snippetText.getStyle()
                    .set("margin", "0")
                    .set("color", "#94a3b8")
                    .set("font-family", "monospace")
                    .set("font-size", "0.875rem")
                    .set("white-space", "pre-wrap")
                    .set("word-break", "break-all");

            snippetDiv.add(snippetLabel, snippetText);
            expandable.add(snippetDiv);
        }

        card.add(header, details, expandable);

        // Toggle expandable on click
        card.getElement().addEventListener("click", e -> {
            expandable.setVisible(!expandable.isVisible());
        });

        // Hover effect
        card.getElement().executeJs("""
            this.addEventListener('mouseenter', () => {
                this.style.transform = 'translateX(4px)';
                this.style.boxShadow = '0 4px 12px rgba(0,0,0,0.1)';
            });
            this.addEventListener('mouseleave', () => {
                this.style.transform = 'translateX(0)';
                this.style.boxShadow = '0 2px 8px rgba(0,0,0,0.05)';
            });
        """);

        return card;
    }

    private String safeJsonString(JsonObject obj, String key) {
        if (obj == null || !obj.hasKey(key)) {
            return "";
        }

        JsonValue value = obj.get(key);

        // Check if the value is null or not a string type
        if (value == null || value.getType() == JsonType.NULL) {
            return "";
        }

        // Only call getString() if it's actually a string type
        if (value.getType() == JsonType.STRING) {
            return obj.getString(key);
        }

        return "";
    }
}
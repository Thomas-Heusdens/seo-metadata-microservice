package be.thomasheusdens.seo_metadata_microservice.gui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "", layout = MainLayout.class)
@PageTitle("SeoAnalyser")
@AnonymousAllowed
public class HomeView extends VerticalLayout {

    private final TextField urlField = new TextField();
    private final Button scanButton = new Button("Analyze SEO");

    public HomeView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setPadding(false);
        setSpacing(false);

        getStyle()
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("position", "relative")
                .set("overflow", "hidden");

        // Animated background elements
        Div bgDecoration1 = new Div();
        bgDecoration1.getStyle()
                .set("position", "absolute")
                .set("width", "500px")
                .set("height", "500px")
                .set("background", "rgba(255, 255, 255, 0.1)")
                .set("border-radius", "50%")
                .set("top", "-250px")
                .set("left", "-250px")
                .set("animation", "float 20s ease-in-out infinite");

        Div bgDecoration2 = new Div();
        bgDecoration2.getStyle()
                .set("position", "absolute")
                .set("width", "400px")
                .set("height", "400px")
                .set("background", "rgba(255, 255, 255, 0.08)")
                .set("border-radius", "50%")
                .set("bottom", "-200px")
                .set("right", "-200px")
                .set("animation", "float 15s ease-in-out infinite reverse");

        // Hero section
        H1 title = new H1("SeoAnalyser");
        title.getStyle()
                .set("margin", "0 0 1rem 0")
                .set("font-size", "3.5rem")
                .set("font-weight", "800")
                .set("color", "white")
                .set("text-align", "center")
                .set("letter-spacing", "-1px")
                .set("text-shadow", "0 2px 20px rgba(0, 0, 0, 0.2)");

        Paragraph tagline = new Paragraph("Unlock your website's full SEO potential");
        tagline.getStyle()
                .set("margin", "0 0 3rem 0")
                .set("font-size", "1.25rem")
                .set("color", "rgba(255, 255, 255, 0.95)")
                .set("text-align", "center")
                .set("font-weight", "400");

        // Search card
        urlField.setPlaceholder("Enter your website URL");
        urlField.setPrefixComponent(VaadinIcon.GLOBE.create());
        urlField.getStyle()
                .set("width", "100%")
                .set("min-width", "300px");

        scanButton.addClickListener(e -> handleScan());
        scanButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        scanButton.setIcon(VaadinIcon.SEARCH.create());
        scanButton.getStyle()
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("border", "none")
                .set("color", "white")
                .set("font-weight", "600")
                .set("box-shadow", "0 4px 15px rgba(0, 0, 0, 0.2)")
                .set("transition", "all 0.3s ease")
                .set("cursor", "pointer")
                .set("min-width", "150px");

        Div inputGroup = new Div(urlField, scanButton);
        inputGroup.getStyle()
                .set("display", "flex")
                .set("gap", "1rem")
                .set("width", "100%")
                .set("flex-wrap", "wrap")
                .set("justify-content", "center");

        Div searchCard = new Div(inputGroup);
        searchCard.getStyle()
                .set("background", "white")
                .set("padding", "2rem")
                .set("border-radius", "16px")
                .set("box-shadow", "0 20px 60px rgba(0, 0, 0, 0.3)")
                .set("max-width", "700px")
                .set("width", "100%")
                .set("animation", "slideUp 0.6s ease-out");

        // Feature cards
        Div feature1 = createFeatureCard(
                VaadinIcon.CHART.create(),
                "Comprehensive Analysis",
                "Get detailed insights into your website's SEO performance"
        );

        Div feature2 = createFeatureCard(
                VaadinIcon.LIGHTBULB.create(),
                "Fast Results",
                "Receive instant feedback and actionable recommendations"
        );

        Div feature3 = createFeatureCard(
                VaadinIcon.TROPHY.create(),
                "Improve Rankings",
                "Boost your search engine visibility with data-driven strategies"
        );

        Div features = new Div(feature1, feature2, feature3);
        features.getStyle()
                .set("display", "flex")
                .set("gap", "1.5rem")
                .set("margin-top", "3rem")
                .set("flex-wrap", "wrap")
                .set("justify-content", "center")
                .set("max-width", "1000px");

        Div content = new Div(title, tagline, searchCard, features);
        content.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center")
                .set("padding", "2rem")
                .set("position", "relative")
                .set("z-index", "1");

        add(bgDecoration1, bgDecoration2, content);

        // Add animations
        getElement().executeJs("""
            const style = document.createElement('style');
            style.textContent = `
                @keyframes slideUp {
                    from {
                        opacity: 0;
                        transform: translateY(30px);
                    }
                    to {
                        opacity: 1;
                        transform: translateY(0);
                    }
                }
               \s
                @keyframes float {
                    0%, 100% {
                        transform: translate(0, 0) rotate(0deg);
                    }
                    33% {
                        transform: translate(30px, -50px) rotate(120deg);
                    }
                    66% {
                        transform: translate(-20px, 20px) rotate(240deg);
                    }
                }
            `;
            document.head.appendChild(style);
       \s""");
    }

    private Div createFeatureCard(com.vaadin.flow.component.icon.Icon icon, String title, String description) {
        icon.getStyle()
                .set("color", "#667eea")
                .set("width", "48px")
                .set("height", "48px")
                .set("margin-bottom", "1rem");

        Span featureTitle = new Span(title);
        featureTitle.getStyle()
                .set("display", "block")
                .set("font-weight", "700")
                .set("font-size", "1.125rem")
                .set("color", "white")
                .set("margin-bottom", "0.5rem");

        Span featureDesc = new Span(description);
        featureDesc.getStyle()
                .set("display", "block")
                .set("color", "rgba(255, 255, 255, 0.85)")
                .set("font-size", "0.938rem")
                .set("line-height", "1.5");

        Div card = new Div(icon, featureTitle, featureDesc);
        card.getStyle()
                .set("background", "rgba(255, 255, 255, 0.15)")
                .set("backdrop-filter", "blur(10px)")
                .set("padding", "2rem")
                .set("border-radius", "12px")
                .set("flex", "1")
                .set("min-width", "250px")
                .set("max-width", "300px")
                .set("text-align", "center")
                .set("border", "1px solid rgba(255, 255, 255, 0.2)")
                .set("transition", "all 0.3s ease")
                .set("animation", "slideUp 0.6s ease-out");

        card.getElement().executeJs("""
            this.addEventListener('mouseenter', () => {
                this.style.transform = 'translateY(-5px)';
                this.style.boxShadow = '0 10px 30px rgba(0, 0, 0, 0.3)';
            });
            this.addEventListener('mouseleave', () => {
                this.style.transform = 'translateY(0)';
                this.style.boxShadow = 'none';
            });
        """);

        return card;
    }

    private void handleScan() {
        getElement().executeJs("""
            const token = sessionStorage.getItem("accessToken");
    
            if (!token) {
                window.location.href = "/login";
                return;
            }
    
            const url = $0.value;
            if (!url || !url.startsWith("http")) {
                alert("Please enter a valid URL starting with http:// or https://");
                return;
            }
    
            // NEW: Redirect to analysis view
            window.location.href = "/analysis?url=" + encodeURIComponent(url);
        """, urlField.getElement());
    }
}
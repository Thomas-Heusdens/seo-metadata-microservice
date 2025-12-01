package be.thomasheusdens.seo_metadata_microservice.seo;

import be.thomasheusdens.seo_metadata_microservice.scraper.ScraperMetadata;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeoAnalysisService {

    public SeoAnalysisResult analyze(ScraperMetadata data) {

        List<SeoCheck> checks = new ArrayList<>();

        // -------------------------------
        // 1. TITLE
        // -------------------------------
        if (isBlank(data.getTitle())) {
            checks.add(SeoCheck.critical(
                    "Title Tag",
                    "Missing <title> tag.",
                    "Add a <title> tag between 45–70 characters.",
                    "<title>Your page title | Brand</title>"
            ));
        } else {
            int length = data.getTitle().length();
            if (length < 45) {
                checks.add(SeoCheck.warning(
                        "Title Tag",
                        "Title too short (" + length + " chars).",
                        "Aim for 45–70 characters with a clear keyword.",
                        "<title>Best Product for X | Brand</title>"
                ));
            } else if (length > 70) {
                checks.add(SeoCheck.warning(
                        "Title Tag",
                        "Title too long (" + length + " chars) — may be truncated in Google.",
                        "Reduce to 45–70 characters.",
                        "<title>Optimized concise title | Brand</title>"
                ));
            } else {
                checks.add(SeoCheck.ok(
                        "Title Tag",
                        "Optimal title length (" + length + " chars)."
                ));
            }
        }

        // -------------------------------
        // 2. META DESCRIPTION
        // -------------------------------
        if (isBlank(data.getDescription())) {
            checks.add(SeoCheck.critical(
                    "Meta Description",
                    "Missing meta description.",
                    "Write a concise 120–160 character description.",
                    "<meta name=\"description\" content=\"Your optimized description here...\" />"
            ));
        } else {
            int len = data.getDescription().length();
            if (len < 120 || len > 160) {
                checks.add(SeoCheck.warning(
                        "Meta Description",
                        "Meta description length is " + len + " chars (recommended 120–160).",
                        "Rewrite to 120–160 characters with a benefit + CTA.",
                        "<meta name=\"description\" content=\"Professional service for ... Learn more today.\" />"
                ));
            } else {
                checks.add(SeoCheck.ok(
                        "Meta Description",
                        "Meta description length is optimal (" + len + " chars)."
                ));
            }
        }

        // -------------------------------
        // 3. CANONICAL
        // -------------------------------
        if (isBlank(data.getCanonicalUrl())) {
            checks.add(SeoCheck.warning(
                    "Canonical Tag",
                    "Missing canonical tag.",
                    "Add a canonical URL to avoid duplicate content.",
                    "<link rel=\"canonical\" href=\"https://yourpage.com\" />"
            ));
        } else {
            checks.add(SeoCheck.ok(
                    "Canonical Tag",
                    "Canonical tag present."
            ));
        }

        // -------------------------------
        // 4. OPEN GRAPH
        // -------------------------------
        addSimplePresenceCheck(checks, data.getOgTitle(), "OG Title",
                "Missing og:title",
                "<meta property=\"og:title\" content=\"Your OG title\" />");

        addSimplePresenceCheck(checks, data.getOgDescription(), "OG Description",
                "Missing og:description",
                "<meta property=\"og:description\" content=\"Your OG description\" />");

        addSimplePresenceCheck(checks, data.getOgImage(), "OG Image",
                "Missing og:image",
                "<meta property=\"og:image\" content=\"https://yourcdn.com/preview.jpg\" />");

        // -------------------------------
        // 5. H1
        // -------------------------------
        if (isBlank(data.getH1())) {
            checks.add(SeoCheck.warning(
                    "H1 Heading",
                    "Missing H1 heading.",
                    "Add a single clear H1 per page.",
                    "<h1>Your main page headline</h1>"
            ));
        } else {
            checks.add(SeoCheck.ok("H1 Heading", "H1 heading found."));
        }

        // -------------------------------
        // 6. FAVICON
        // -------------------------------
        if (isBlank(data.getFavicon())) {
            checks.add(SeoCheck.warning(
                    "Favicon",
                    "Missing favicon.",
                    "Add a favicon for branding consistency.",
                    "<link rel=\"icon\" href=\"/favicon.ico\" />"
            ));
        } else {
            checks.add(SeoCheck.ok("Favicon", "Favicon found."));
        }

        // -------------------------------
        // 7. HREFLANG
        // -------------------------------
        if (data.getHreflangs().isEmpty()) {
            checks.add(SeoCheck.warning(
                    "Hreflang",
                    "No hreflang tags detected.",
                    "Add hreflangs for multilingual sites.",
                    "<link rel=\"alternate\" hreflang=\"fr\" href=\"https://site.com/fr\" />"
            ));
        } else {
            checks.add(SeoCheck.ok(
                    "Hreflang",
                    "Hreflang tags found (" + data.getHreflangs().size() + ")."
            ));
        }

        // -------------------------------
        // 8. ROBOTS
        // -------------------------------
        if (data.getRobots().contains("noindex")) {
            checks.add(SeoCheck.critical(
                    "Robots Tag",
                    "This page is marked as noindex — Google will NOT index it.",
                    "Remove `noindex` unless intentional.",
                    "<meta name=\"robots\" content=\"index, follow\">"
            ));
        } else {
            checks.add(SeoCheck.ok("Robots Tag", "Robots tag looks safe."));
        }

        // -------------------------------
        // 9. VIEWPORT
        // -------------------------------
        if (isBlank(data.getViewport()) || !data.getViewport().contains("device-width")) {
            checks.add(SeoCheck.warning(
                    "Viewport",
                    "Missing or incorrect viewport tag — page may display poorly on mobile.",
                    "Use the standard responsive viewport.",
                    "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
            ));
        } else {
            checks.add(SeoCheck.ok("Viewport", "Viewport is valid."));
        }

        // -------------------------------
        // 10. JSON-LD
        // -------------------------------
        if (data.getJsonLdList().isEmpty()) {
            checks.add(SeoCheck.warning(
                    "JSON-LD Structured Data",
                    "No JSON-LD structured data found.",
                    "Add Schema.org JSON-LD for richer Google results.",
                    "<script type=\"application/ld+json\">{ ... }</script>"
            ));
        } else {
            checks.add(SeoCheck.ok(
                    "JSON-LD Structured Data",
                    "Structured data detected (" + data.getJsonLdList().size() + " blocks)."
            ));
        }

        // -------------------------------
        // 11. TWITTER TAGS
        // -------------------------------
        addSimplePresenceCheck(checks, data.getTwitterTitle(), "Twitter Title",
                "Missing twitter:title",
                "<meta name=\"twitter:title\" content=\"Title\" />");

        addSimplePresenceCheck(checks, data.getTwitterDescription(), "Twitter Description",
                "Missing twitter:description",
                "<meta name=\"twitter:description\" content=\"Description\" />");

        addSimplePresenceCheck(checks, data.getTwitterImage(), "Twitter Image",
                "Missing twitter:image",
                "<meta name=\"twitter:image\" content=\"https://yourcdn.com/preview.jpg\" />");

        // -------------------------------
        // 12. INTERNAL LINKS
        // -------------------------------
        if (data.getInternalLinksCount() < 5) {
            checks.add(SeoCheck.warning(
                    "Internal Links",
                    "Only " + data.getInternalLinksCount() + " internal links found.",
                    "Add more internal links to improve navigation & SEO.",
                    "<a href=\"/product\">Product Page</a>"
            ));
        } else {
            checks.add(SeoCheck.ok(
                    "Internal Links",
                    "Healthy number of internal links (" + data.getInternalLinksCount() + ")."
            ));
        }

        // -------------------------------
        // 13. EXTERNAL LINKS
        // -------------------------------
        if (data.getExternalLinksCount() == 0) {
            checks.add(SeoCheck.warning(
                    "External Links",
                    "No external links found — may reduce domain trust.",
                    "Link to relevant authoritative sources.",
                    "<a href=\"https://wikipedia.org/...\">See more</a>"
            ));
        } else {
            checks.add(SeoCheck.ok(
                    "External Links",
                    "External links found (" + data.getExternalLinksCount() + ")."
            ));
        }

        // -------------------------------
        // 14. ALT TEXT
        // -------------------------------
        if (data.getMissingAltCount() > 0) {
            checks.add(SeoCheck.warning(
                    "Image Alt Text",
                    data.getMissingAltCount() + " images missing alt text.",
                    "Add descriptive alt attributes for accessibility & SEO.",
                    "<img src=\"img.jpg\" alt=\"Description of the image\" />"
            ));
        } else {
            checks.add(SeoCheck.ok("Image Alt Text", "All images have alt text."));
        }

        // -------------------------------
        // 15. WORD COUNT
        // -------------------------------
        if (data.getWordCount() < 300) {
            checks.add(SeoCheck.warning(
                    "Page Content Length",
                    "Low word count (" + data.getWordCount() + ").",
                    "Aim for 300+ words of meaningful content.",
                    "<p>Your expanded content here...</p>"
            ));
        } else {
            checks.add(SeoCheck.ok("Page Content Length",
                    "Healthy word count (" + data.getWordCount() + ")."));
        }

        return new SeoAnalysisResult(data.getUrl(), checks);
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private void addSimplePresenceCheck(List<SeoCheck> list, String value, String label, String missingMessage, String example) {
        if (isBlank(value)) {
            list.add(SeoCheck.warning(label, missingMessage, "Add this tag:", example));
        } else {
            list.add(SeoCheck.ok(label, label + " exists."));
        }
    }
}
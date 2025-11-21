package be.thomasheusdens.seo_metadata_microservice.seo;

import be.thomasheusdens.seo_metadata_microservice.scraper.ScraperMetadata;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeoAnalysisService {

    public SeoAnalysisResult analyze(ScraperMetadata data) {

        List<String> warnings = new ArrayList<>();
        List<String> positives = new ArrayList<>();

        // --- TITLE ---
        if (data.getTitle() == null || data.getTitle().isBlank()) {
            warnings.add("Missing <title> tag.");
        } else {
            int length = data.getTitle().length();
            if (length < 45 || length > 70) {
                warnings.add("Title length should be 45–70 characters. Current length: " + length);
            } else {
                positives.add("Title length is optimal (" + length + " chars).");
            }
        }

        // --- META DESCRIPTION ---
        if (data.getDescription() == null || data.getDescription().isBlank()) {
            warnings.add("Missing meta description.");
        } else {
            int len = data.getDescription().length();
            if (len < 120 || len > 160) {
                warnings.add("Meta description should be 120–160 characters. Current length: " + len);
            } else {
                positives.add("Meta description length is optimal (" + len + " chars).");
            }
        }

        // --- CANONICAL ---
        if (data.getCanonicalUrl() == null || data.getCanonicalUrl().isBlank()) {
            warnings.add("Missing canonical URL.");
        } else {
            positives.add("Canonical tag exists.");
        }

        // --- KEYWORDS ---
        if (data.getKeywords() != null && !data.getKeywords().isBlank()) {
            positives.add("Meta keywords found.");
        }

        // --- OG TITLE ---
        if (data.getOgTitle() == null || data.getOgTitle().isBlank()) {
            warnings.add("Missing og:title.");
        } else {
            positives.add("og:title exists.");
        }

        // --- OG DESCRIPTION ---
        if (data.getOgDescription() == null || data.getOgDescription().isBlank()) {
            warnings.add("Missing og:description.");
        } else {
            positives.add("og:description exists.");
        }

        // --- OG IMAGE ---
        if (data.getOgImage() == null || data.getOgImage().isBlank()) {
            warnings.add("Missing og:image.");
        } else {
            positives.add("og:image found.");
        }

        // --- H1 ---
        if (data.getH1() == null || data.getH1().isBlank()) {
            warnings.add("Missing H1 heading.");
        } else {
            positives.add("H1 heading found.");
        }

        // Multiple H1 detector
        // If H1 text length is very long, they might have multiple concatenated H1s
        if (data.getH1() != null && data.getH1().split(" ").length > 20) {
            warnings.add("Possible multiple H1 tags or unclear H1 structure.");
        }

        // --- FAVICON ---
        if (data.getFavicon() == null || data.getFavicon().isBlank()) {
            warnings.add("Missing favicon.");
        } else {
            positives.add("Favicon exists.");
        }

        // --- HREFLANG ---
        if (data.getHreflangs() != null && !data.getHreflangs().isEmpty()) {
            positives.add("Hreflang tags found (" + data.getHreflangs().size() + ").");
        }

        // --- ROBOTS ---
        if (data.getRobots() != null && data.getRobots().contains("noindex")) {
            warnings.add("Page contains noindex — page will not appear in Google.");
        }

        // --- VIEWPORT ---
        if (data.getViewport() == null || !data.getViewport().contains("width=device-width")) {
            warnings.add("Missing or incorrect viewport tag — poor mobile rendering.");
        } else {
            positives.add("Viewport tag is valid.");
        }

        // --- JSON-LD ---
        if (data.getJsonLdList() != null && !data.getJsonLdList().isEmpty()) {
            positives.add("JSON-LD structured data found.");
        } else {
            warnings.add("Missing structured data (JSON-LD).");
        }

        // --- TWITTER TAGS ---
        if (data.getTwitterTitle() == null || data.getTwitterTitle().isBlank()) {
            warnings.add("Missing twitter:title.");
        }
        if (data.getTwitterDescription() == null || data.getTwitterDescription().isBlank()) {
            warnings.add("Missing twitter:description.");
        }
        if (data.getTwitterImage() == null || data.getTwitterImage().isBlank()) {
            warnings.add("Missing twitter:image.");
        }

        // --- INTERNAL LINKS ---
        if (data.getInternalLinksCount() < 5) {
            warnings.add("Few internal links detected (" + data.getInternalLinksCount() + ").");
        } else {
            positives.add("Healthy internal linking (" + data.getInternalLinksCount() + ").");
        }

        // --- EXTERNAL LINKS ---
        if (data.getExternalLinksCount() == 0) {
            warnings.add("No external links found. Might reduce authority.");
        } else {
            positives.add("External links found (" + data.getExternalLinksCount() + ").");
        }

        // --- IMAGES & ALT TEXT ---
        if (data.getMissingAltCount() > 0) {
            warnings.add(data.getMissingAltCount() + " images missing alt attributes.");
        } else {
            positives.add("All images have alt attributes.");
        }

        // --- WORD COUNT ---
        if (data.getWordCount() < 300) {
            warnings.add("Low content (" + data.getWordCount() + " words). Recommended 300+.");
        } else {
            positives.add("Good content length (" + data.getWordCount() + " words).");
        }

        return new SeoAnalysisResult(
                data.getUrl(),
                warnings,
                positives
        );
    }
}

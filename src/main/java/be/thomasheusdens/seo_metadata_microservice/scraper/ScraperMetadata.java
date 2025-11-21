package be.thomasheusdens.seo_metadata_microservice.scraper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ScraperMetadata {

    private String url;

    // --- BASIC SEO TAGS ---
    private String title;
    private String description;
    private String canonicalUrl;
    private String keywords;

    // --- OPEN GRAPH TAGS ---
    private String ogTitle;
    private String ogDescription;
    private String ogImage;

    // --- HEADINGS ---
    private String h1;

    // --- ICONS ---
    private String favicon;
    private String appleTouchIcon;

    // --- HREFLANG TAGS ---
    private List<String> hreflangs;

    // --- ROBOTS / VIEWPORT ---
    private String robots;
    private String viewport;

    // --- STRUCTURED DATA ---
    private List<String> jsonLdList;

    // --- TWITTER TAGS ---
    private String twitterTitle;
    private String twitterDescription;
    private String twitterImage;

    // --- LINKS ---
    private int internalLinksCount;
    private int externalLinksCount;

    // --- IMAGES ---
    private List<String> images;
    private long missingAltCount;

    // --- PAGE CONTENT ---
    private int wordCount;
}
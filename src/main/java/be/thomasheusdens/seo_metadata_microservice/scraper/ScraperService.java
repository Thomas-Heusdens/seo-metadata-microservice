package be.thomasheusdens.seo_metadata_microservice.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScraperService {

    public ScraperMetadata extractMetadata(String url) throws Exception {
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(10000)
                .get();

        // -------- BASIC SEO TAGS --------
        String title = doc.title();

        String description = doc.select("meta[name=description]").attr("content");

        String canonical = doc.select("link[rel=canonical]").attr("href");

        String keywords = doc.select("meta[name=keywords]").attr("content");


        // -------- OPEN GRAPH TAGS --------
        String ogTitle = doc.select("meta[property=og:title]").attr("content");

        String ogDescription = doc.select("meta[property=og:description]").attr("content");

        String ogImage = doc.select("meta[property=og:image]").attr("content");


        // -------- HEADINGS --------
        String h1 = doc.select("h1").text();


        // -------- ICONS --------
        String favicon = doc.select("link[rel=icon]").attr("href");
        if (favicon.isEmpty()) {
            favicon = doc.select("link[rel=shortcut icon]").attr("href");
        }

        String appleTouchIcon = doc.select("link[rel=apple-touch-icon]").attr("href");


        // -------- INTERNATIONALIZATION --------
        // NEW: hreflang list (each link element)
        List<String> hreflangs = doc.select("link[rel=alternate][hreflang]")
                .stream()
                .map(el -> el.attr("hreflang") + " -> " + el.attr("href"))
                .collect(Collectors.toList());


        // -------- ROBOTS & VIEWPORT --------
        String robots = doc.select("meta[name=robots]").attr("content");

        String viewport = doc.select("meta[name=viewport]").attr("content");


        // -------- STRUCTURED DATA --------
        List<String> jsonLdList = doc.select("script[type=application/ld+json]")
                .stream()
                .map(Element::data)
                .filter(text -> !text.isBlank())
                .collect(Collectors.toList());



        // -------- TWITTER TAGS --------
        String twitterTitle = doc.select("meta[name=twitter:title]").attr("content");
        String twitterDescription = doc.select("meta[name=twitter:description]").attr("content");
        String twitterImage = doc.select("meta[name=twitter:image]").attr("content");


        // -------- LINKS --------
        int internalLinksCount = doc.select("a[href^='/']").size();

        int externalLinksCount = doc.select("a[href^='http']").size();


        // -------- IMAGES & ALT TEXT --------
        List<String> images = doc.select("img")
                .eachAttr("src");

        long missingAltCount = doc.select("img")
                .stream()
                .filter(img -> img.attr("alt").isBlank())
                .count();


        // -------- PAGE WORD COUNT --------
        int wordCount = doc.body().text().split("\\s+").length;


        // -------- RETURN METADATA --------
        return new ScraperMetadata(
                url,
                title,
                description,
                canonical,
                keywords,
                ogTitle,
                ogDescription,
                ogImage,
                h1,
                favicon,
                appleTouchIcon,
                hreflangs,
                robots,
                viewport,
                jsonLdList,
                twitterTitle,
                twitterDescription,
                twitterImage,
                internalLinksCount,
                externalLinksCount,
                images,
                missingAltCount,
                wordCount
        );
    }
}
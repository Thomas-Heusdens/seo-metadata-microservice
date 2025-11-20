package be.thomasheusdens.seo_metadata_microservice.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

@Service
public class SeoService {

    public SeoMetadata extractMetadata(String url) throws Exception {
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(10000)
                .get();

        String title = doc.title();

        String description = doc.select("meta[name=description]")
                .attr("content");

        String canonical = doc.select("link[rel=canonical]")
                .attr("href");

        String keywords = doc.select("meta[name=keywords]")
                .attr("content");

        String ogTitle = doc.select("meta[property=og:title]")
                .attr("content");

        String ogDescription = doc.select("meta[property=og:description]")
                .attr("content");

        String ogImage = doc.select("meta[property=og:image]")
                .attr("content");

        String h1 = doc.select("h1").text();

        return new SeoMetadata(
                url,
                title,
                description,
                canonical,
                keywords,
                ogTitle,
                ogDescription,
                ogImage,
                h1
        );
    }
}
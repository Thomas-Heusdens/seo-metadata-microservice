package be.thomasheusdens.seo_metadata_microservice.scraper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SeoMetadata {

    private String url;
    private String title;
    private String description;
    private String canonicalUrl;
    private String keywords;
    private String ogTitle;
    private String ogDescription;
    private String ogImage;
    private String h1;
}
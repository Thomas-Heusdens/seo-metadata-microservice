package be.thomasheusdens.seo_metadata_microservice.seo;

import be.thomasheusdens.seo_metadata_microservice.scraper.ScraperMetadata;
import be.thomasheusdens.seo_metadata_microservice.scraper.ScraperService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SeoController {

    private final ScraperService scraperService;
    private final SeoAnalysisService analysisService;

    public SeoController(ScraperService scraperService, SeoAnalysisService analysisService) {
        this.scraperService = scraperService;
        this.analysisService = analysisService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/api/seo/analyze")
    public SeoAnalysisResult analyze(@RequestParam String url) throws Exception {
        if (url == null || url.equals("undefined") || url.isBlank()) {
            throw new IllegalArgumentException("Valid URL is required");
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new IllegalArgumentException("URL must start with http:// or https://");
        }

        ScraperMetadata metadata = scraperService.extractMetadata(url);
        return analysisService.analyze(metadata);
    }
}
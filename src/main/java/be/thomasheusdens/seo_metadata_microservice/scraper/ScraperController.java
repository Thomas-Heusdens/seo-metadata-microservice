package be.thomasheusdens.seo_metadata_microservice.scraper;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScraperController {

    private final ScraperService scraperService;

    public ScraperController(ScraperService scraperService) {
        this.scraperService = scraperService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/api/scraper/extract")
    public ScraperMetadata extract(@RequestParam String url) throws Exception {
        return scraperService.extractMetadata(url);
    }
}
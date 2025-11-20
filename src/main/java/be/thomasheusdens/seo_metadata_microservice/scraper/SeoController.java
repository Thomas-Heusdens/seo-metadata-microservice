package be.thomasheusdens.seo_metadata_microservice.scraper;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SeoController {

    private final SeoService seoService;

    public SeoController(SeoService seoService) {
        this.seoService = seoService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/api/scraper/extract")
    public SeoMetadata extract(@RequestParam String url) throws Exception {
        return seoService.extractMetadata(url);
    }
}
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
    @GetMapping("/api/scraper/analyze")
    public SeoAnalysisResult analyze(@RequestParam String url) throws Exception {
        ScraperMetadata metadata = scraperService.extractMetadata(url);
        return analysisService.analyze(metadata);
    }
}
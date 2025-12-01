package be.thomasheusdens.seo_metadata_microservice.seo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SeoAnalysisResult {
    private String url;
    private List<SeoCheck> checks;
}
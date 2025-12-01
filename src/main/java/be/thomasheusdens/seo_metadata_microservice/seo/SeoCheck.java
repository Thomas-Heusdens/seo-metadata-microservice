package be.thomasheusdens.seo_metadata_microservice.seo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeoCheck {
    private String label;
    private String status;
    private String message;
    private String recommendation;
    private String snippet;

    public static SeoCheck ok(String label, String message){
        return new SeoCheck(label, "OK", message, null, null);
    }
    public static SeoCheck warning(String label, String message, String rec, String snippet){
        return new SeoCheck(label, "WARNING", message, rec, snippet);
    }
    public static SeoCheck critical(String label, String message, String rec, String snippet){
        return new SeoCheck(label, "CRITICAL", message, rec, snippet);
    }
}
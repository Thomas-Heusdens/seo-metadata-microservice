package be.thomasheusdens.seo_metadata_microservice.auth.refresh;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TokenRefreshRequest {
    private String refreshToken;
}
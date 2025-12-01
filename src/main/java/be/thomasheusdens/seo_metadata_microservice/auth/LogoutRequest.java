package be.thomasheusdens.seo_metadata_microservice.auth;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LogoutRequest {
    private String refreshToken;
    private boolean logoutAll = false;
}
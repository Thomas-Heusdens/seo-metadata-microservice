package be.thomasheusdens.seo_metadata_microservice.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String username;
    private String password;
}
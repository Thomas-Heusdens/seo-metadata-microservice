package be.thomasheusdens.seo_metadata_microservice.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterResponse {
    private String message;
    private String username;

    public RegisterResponse(String message, String username) {
        this.message = message;
        this.username = username;
    }
}
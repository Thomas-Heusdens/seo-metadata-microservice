package be.thomasheusdens.seo_metadata_microservice.auth;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class LoginResponse {
    @Setter
    @Getter
    private String jwtToken;

    @Setter
    @Getter
    private String username;
    @Setter
    @Getter
    private List<String> roles;

    public LoginResponse(String username, List<String> roles, String jwtToken){
        this.username = username;
        this.roles = roles;
        this.jwtToken = jwtToken;
    }
}

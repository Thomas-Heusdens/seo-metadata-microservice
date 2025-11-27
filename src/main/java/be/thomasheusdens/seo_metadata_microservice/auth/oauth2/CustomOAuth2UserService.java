package be.thomasheusdens.seo_metadata_microservice.auth.oauth2;

import be.thomasheusdens.seo_metadata_microservice.user.Role;
import be.thomasheusdens.seo_metadata_microservice.user.RoleRepository;
import be.thomasheusdens.seo_metadata_microservice.user.User;
import be.thomasheusdens.seo_metadata_microservice.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomOAuth2UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {

        OAuth2User oauthUser = super.loadUser(request);
        String email = oauthUser.getAttribute("email");

        if (email == null) {
            throw new OAuth2AuthenticationException("Email not provided by provider");
        }

        User user = userRepository.findByUsername(email).orElse(null);

        if (user == null) {
            // NEW USER → Create it
            user = new User();
            user.setUsername(email);
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // dummy password

            Role userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("Role USER not found"));
            user.getRoles().add(userRole);

            userRepository.save(user);
        }

        return oauthUser;
    }
}
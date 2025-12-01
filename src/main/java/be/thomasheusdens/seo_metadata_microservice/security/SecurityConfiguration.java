package be.thomasheusdens.seo_metadata_microservice.security;

import be.thomasheusdens.seo_metadata_microservice.auth.refresh.RefreshToken;
import be.thomasheusdens.seo_metadata_microservice.auth.refresh.RefreshTokenService;
import be.thomasheusdens.seo_metadata_microservice.jwt.AuthEntryPointJwt;
import be.thomasheusdens.seo_metadata_microservice.jwt.AuthTokenFilter;
import be.thomasheusdens.seo_metadata_microservice.jwt.JwtUtils;
import be.thomasheusdens.seo_metadata_microservice.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    private final AuthEntryPointJwt unauthorizedHandler;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtils jwtUtils;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService;

    public SecurityConfiguration(
            AuthEntryPointJwt unauthorizedHandler,
            UserRepository userRepository,
            RefreshTokenService refreshTokenService,
            JwtUtils jwtUtils,
            OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService
    ) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.jwtUtils = jwtUtils;
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/refresh",
                                "/oauth2/success",
                                "/oauth2/**",
                                "/",
                                "/login",
                                "/analysis",
                                "/register",

                                "/VAADIN/**",
                                "/frontend/**",
                                "/themes/**",
                                "/images/**",
                                "/icons/**",
                                "/@js/**",
                                "/js/**",
                                "/js/auth-interceptor.js",
                                "/auth-interceptor.js",
                                "/@fs/**",
                                "/webjars/**",
                                "/manifest.webmanifest",
                                "/sw.js",
                                "/offline.html",
                                "/frontend-es5/**",
                                "/frontend-es6/**",
                                "/@vite/**",
                                "/@vaadin/**",
                                "/vaadin/**",
                                "/resources/**",
                                "/connect/**",
                                "/manifest.webmanifest"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(e -> e.authenticationEntryPoint(unauthorizedHandler))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .userInfoEndpoint(info -> info.userService(customOAuth2UserService))
                        .successHandler((request, response, authentication) -> {

                            String email = ((OAuth2User) authentication.getPrincipal()).getAttribute("email");

                            var user = userRepository.findByUsername(email)
                                    .orElseThrow(() -> new RuntimeException("User not found"));

                            UserDetails details = org.springframework.security.core.userdetails.User
                                    .withUsername(user.getUsername())
                                    .password(user.getPassword())
                                    .authorities(user.getRoles().stream()
                                            .map(r -> "ROLE_" + r.getName())
                                            .toArray(String[]::new))
                                    .build();

                            String accessToken = jwtUtils.generateAccessToken(details);

                            RefreshToken refreshToken = refreshTokenService.createRefreshToken(
                                    user.getUsername(),
                                    request.getHeader("User-Agent")
                            );

                            ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                                    .httpOnly(true)
                                    .secure(true)
                                    .sameSite("None")
                                    .path("/")
                                    .maxAge(7 * 24 * 60 * 60)
                                    .build();

                            response.addHeader("Set-Cookie", cookie.toString());

                            response.sendRedirect("/oauth2/success?token=" + accessToken);
                        })
                )
                .cors(withDefaults())
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(s
                        -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
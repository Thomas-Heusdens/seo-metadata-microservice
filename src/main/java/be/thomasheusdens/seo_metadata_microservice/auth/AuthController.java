package be.thomasheusdens.seo_metadata_microservice.auth;

import be.thomasheusdens.seo_metadata_microservice.auth.refresh.*;
import be.thomasheusdens.seo_metadata_microservice.jwt.JwtUtils;
import be.thomasheusdens.seo_metadata_microservice.user.Role;
import be.thomasheusdens.seo_metadata_microservice.user.RoleRepository;
import be.thomasheusdens.seo_metadata_microservice.user.User;
import be.thomasheusdens.seo_metadata_microservice.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private final RefreshTokenService refreshTokenService;

    @Value("${spring.app.refreshTokenCookieName:refreshToken}")
    private String refreshTokenCookieName;

    @Value("${spring.app.refreshTokenExpirationMs}")
    private long refreshTokenExpirationMs;

    public AuthController(UserRepository userRepository, RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder, JwtUtils jwtUtils,
                          AuthenticationManager authenticationManager,
                          RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest,
                                              HttpServletRequest request,
                                              HttpServletResponse response) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Generate access token
        String accessToken = jwtUtils.generateAccessToken(userDetails);

        // Create refresh token in DB
        String deviceInfo = request.getHeader("User-Agent");
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(
                userDetails.getUsername(), deviceInfo
        );

        // Set refresh token as HttpOnly cookie
        addRefreshTokenCookie(response, refreshToken.getToken());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        LoginResponse loginResponse = new LoginResponse(
                userDetails.getUsername(),
                roles,
                accessToken,
                refreshToken.getToken() // Also return in body for mobile clients
        );

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        if (registerRequest.getUsername() == null || registerRequest.getPassword() == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Username and password are required");
        }
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Username is already taken");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role USER not found"));

        user.getRoles().add(userRole);

        userRepository.save(user);

        return ResponseEntity.ok(new RegisterResponse(
                "User successfully registered",
                user.getUsername()
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @RequestBody(required = false) TokenRefreshRequest request,
            @CookieValue(name = "refreshToken", required = false) String cookieToken,
            HttpServletResponse response) {

        final String oldRefreshToken = (cookieToken != null)
                ? cookieToken
                : (request != null ? request.getRefreshToken() : null);

        if (oldRefreshToken == null || oldRefreshToken.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Refresh token is required"));
        }

        return refreshTokenService.findByToken(oldRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(oldToken -> {

                    // 🔥 Deletes old token + creates and saves new one
                    RefreshToken newToken = refreshTokenService.rotateToken(oldToken);

                    // 🔥 Set new cookie
                    addRefreshTokenCookie(response, newToken.getToken());

                    User user = newToken.getUser();
                    UserDetails userDetails = org.springframework.security.core.userdetails.User
                            .withUsername(user.getUsername())
                            .password(user.getPassword())
                            .authorities(user.getRoles().stream()
                                    .map(r -> "ROLE_" + r.getName())
                                    .toArray(String[]::new))
                            .build();

                    // 🔥 New access token
                    String newAccessToken = jwtUtils.generateAccessToken(userDetails);

                    return ResponseEntity.ok(
                            new TokenRefreshResponse(
                                    newAccessToken,
                                    newToken.getToken()
                            )
                    );
                })
                .orElseThrow(() -> new TokenRefreshException("Refresh token not found in database"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(
            @CookieValue(name = "refreshToken", required = false) String cookieToken,
            @RequestBody(required = false) LogoutRequest body,
            HttpServletResponse response) {

        String refreshToken = cookieToken;

        if (refreshToken == null && body != null) {
            refreshToken = body.getRefreshToken();
        }

        if (body != null && body.isLogoutAll()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                refreshTokenService.deleteAllUserTokens(auth.getName());
            }
        } else if (refreshToken != null) {
            refreshTokenService.deleteToken(refreshToken);
        }

        ResponseCookie cookie = ResponseCookie.from(refreshTokenCookieName, "")
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(refreshTokenCookieName, token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(refreshTokenExpirationMs / 1000)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
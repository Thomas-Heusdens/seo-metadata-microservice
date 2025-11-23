package be.thomasheusdens.seo_metadata_microservice.auth;

import be.thomasheusdens.seo_metadata_microservice.auth.refresh.*;
import be.thomasheusdens.seo_metadata_microservice.jwt.JwtUtils;
import be.thomasheusdens.seo_metadata_microservice.user.Role;
import be.thomasheusdens.seo_metadata_microservice.user.RoleRepository;
import be.thomasheusdens.seo_metadata_microservice.user.User;
import be.thomasheusdens.seo_metadata_microservice.user.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//once we add more logic to the login and register, we'll need to have a service
//example utility of service: password strength, username rules, exceptions, resetting your password, adding OAuth or admin-creating-users

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
        String ipAddress = getClientIp(request);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(
                userDetails.getUsername(), deviceInfo, ipAddress
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

                    RefreshToken newToken = refreshTokenService.rotateToken(oldToken);

                    addRefreshTokenCookie(response, newToken.getToken());

                    User user = newToken.getUser();
                    UserDetails userDetails = org.springframework.security.core.userdetails.User
                            .withUsername(user.getUsername())
                            .password(user.getPassword())
                            .authorities(user.getRoles().stream()
                                    .map(r -> "ROLE_" + r.getName())
                                    .toArray(String[]::new))
                            .build();

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
    public ResponseEntity<?> logoutUser(@RequestBody(required = false) LogoutRequest logoutRequest,
                                        @CookieValue(name = "refreshToken", required = false) String cookieToken,
                                        HttpServletResponse response) {
        String refreshToken = cookieToken;
        if (refreshToken == null && logoutRequest != null) {
            refreshToken = logoutRequest.getRefreshToken();
        }

        if (logoutRequest != null && logoutRequest.isLogoutAll()) {
            // Logout from all devices
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                refreshTokenService.revokeAllUserTokens(auth.getName());
            }
        } else if (refreshToken != null) {
            // Logout from current device only
            refreshTokenService.revokeToken(refreshToken);
        }

        // Clear the cookie
        clearRefreshTokenCookie(response);

        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(refreshTokenCookieName, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Set to false for local development without HTTPS
        cookie.setPath("/");
        cookie.setMaxAge((int) (refreshTokenExpirationMs / 1000));
        // cookie.setAttribute("SameSite", "Strict"); // Uncomment for CSRF protection
        response.addCookie(cookie);
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(refreshTokenCookieName, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
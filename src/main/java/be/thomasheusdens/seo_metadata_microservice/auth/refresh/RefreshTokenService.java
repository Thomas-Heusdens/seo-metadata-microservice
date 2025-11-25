package be.thomasheusdens.seo_metadata_microservice.auth.refresh;

import be.thomasheusdens.seo_metadata_microservice.user.User;
import be.thomasheusdens.seo_metadata_microservice.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${spring.app.refreshTokenExpirationMs}")
    private long refreshTokenExpirationMs;

    @Value("${spring.app.maxSessionsPerUser:5}")
    private int maxSessionsPerUser;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RefreshToken createRefreshToken(String username, String deviceInfo) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        long activeCount = refreshTokenRepository.countByUser(user);

        if (activeCount >= maxSessionsPerUser) {
            refreshTokenRepository.deleteAllByUser(user);
        }

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpirationMs));
        refreshToken.setDeviceInfo(deviceInfo);
        refreshToken.setCreatedAt(Instant.now());

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException("Refresh token was expired. Please login again.");
        }
        return token;
    }

    @Transactional
    public void deleteToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    @Transactional
    public void deleteAllUserTokens(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        refreshTokenRepository.deleteAllByUser(user);
    }

    @Transactional
    public RefreshToken rotateToken(RefreshToken oldToken) {
        refreshTokenRepository.delete(oldToken);

        RefreshToken newToken = new RefreshToken();
        newToken.setUser(oldToken.getUser());
        newToken.setToken(UUID.randomUUID().toString());
        newToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpirationMs));
        newToken.setDeviceInfo(oldToken.getDeviceInfo());
        newToken.setCreatedAt(Instant.now());

        return refreshTokenRepository.save(newToken);
    }

    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteAllExpired(Instant.now());
    }
}
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
    public RefreshToken createRefreshToken(String username, String deviceInfo, String ipAddress) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Limit active sessions per user
        long activeCount = refreshTokenRepository.countByUserAndRevokedFalse(user);
        if (activeCount >= maxSessionsPerUser) {
            // Revoke oldest sessions if limit exceeded
            refreshTokenRepository.revokeAllByUser(user);
        }

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpirationMs));
        refreshToken.setDeviceInfo(deviceInfo);
        refreshToken.setIpAddress(ipAddress);
        refreshToken.setCreatedAt(Instant.now());

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (!token.isValid()) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException("Refresh token was expired or revoked. Please login again.");
        }
        return token;
    }

    @Transactional
    public void revokeToken(String token) {
        refreshTokenRepository.revokeByToken(token);
    }

    @Transactional
    public void revokeAllUserTokens(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        refreshTokenRepository.revokeAllByUser(user);
    }

    @Transactional
    public RefreshToken rotateToken(RefreshToken oldToken) {
        oldToken.setRevoked(true);
        refreshTokenRepository.save(oldToken);

        RefreshToken newToken = new RefreshToken();
        newToken.setUser(oldToken.getUser());
        newToken.setToken(UUID.randomUUID().toString());
        newToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpirationMs));
        newToken.setDeviceInfo(oldToken.getDeviceInfo());
        newToken.setIpAddress(oldToken.getIpAddress());
        newToken.setCreatedAt(Instant.now());
        return refreshTokenRepository.save(newToken);
    }


    // Cleanup expired tokens daily
    @Scheduled(cron = "0 0 2 * * ?") // Runs at 2 AM daily
    @Transactional
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteAllExpired(Instant.now());
    }
}
package com.beetclick.authservice.service;

import com.beetclick.authservice.entity.RefreshToken;
import com.beetclick.common.entity.Role;
import com.beetclick.authservice.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final TokenHashService hasher;
    private final long ttlDays;
    private final SecureRandom random = new SecureRandom();

    public RefreshTokenService(
            RefreshTokenRepository repo,
            TokenHashService hasher,
            @Value("${app.refresh.ttl-days:30}") long ttlDays
    ) {
        this.repo = repo;
        this.hasher = hasher;
        this.ttlDays = ttlDays;
    }

    public record Pair(String raw, RefreshToken entity) {}

    public Pair create(UUID userId, Role role) {
        String raw = generateRaw();
        String hash = hasher.hash(raw);

        RefreshToken rt = new RefreshToken();
        rt.setUserId(userId);
        rt.setRole(role);
        rt.setTokenHash(hash);
        rt.setExpiresAt(Instant.now().plus(ttlDays, ChronoUnit.DAYS));

        repo.save(rt);
        return new Pair(raw, rt);
    }

    public RefreshToken requireValid(String raw) {
        String hash = hasher.hash(raw);
        RefreshToken rt = repo.findByTokenHash(hash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (rt.isRevoked() || rt.isExpired()) {
            throw new IllegalArgumentException("Refresh token revoked/expired");
        }
        return rt;
    }

    @Transactional
    public void revoke(String raw) {
        String hash = hasher.hash(raw);
        repo.findByTokenHash(hash).ifPresent(rt -> {
            if (!rt.isRevoked()) {
                rt.setRevoked(true);
                rt.setRevokedAt(Instant.now());
            }
        });
    }

    @Transactional
    public Pair rotate(String raw) {
        RefreshToken current = requireValid(raw);
        current.setRevoked(true);
        current.setRevokedAt(Instant.now());

        return create(current.getUserId(), current.getRole());
    }

    private String generateRaw() {
        byte[] bytes = new byte[48];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}

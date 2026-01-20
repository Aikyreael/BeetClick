package com.beetclick.authservice.service;

import com.beetclick.authservice.entity.AuthUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}") // 24 hours
    private long jwtExpiration;

    private SecretKey getSignInKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        logger.debug("Generating JWT for email={}", userDetails.getUsername());
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        AuthUser user = (AuthUser) userDetails;
        extraClaims.put("role", user.getRole().name());
        extraClaims.put("userId", user.getId());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        String token = Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSignInKey())
                .compact();

        logger.debug("JWT generated for userId={}, email={}", user.getId(), user.getEmail());
        return token;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            boolean valid = (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
            if (!valid) {
                logger.debug("Invalid JWT attempt for userId={}, email={}",
                        ((AuthUser) userDetails).getId(), userDetails.getUsername());
            } else {
                logger.debug("JWT valid for userId={}, email={}",
                        ((AuthUser) userDetails).getId(), userDetails.getUsername());
            }
            return valid;
        } catch (ExpiredJwtException ex) {
            logger.debug("Expired JWT token for email={}", userDetails.getUsername());
        } catch (JwtException ex) {
            logger.debug("Invalid JWT token for email={}: {}", userDetails.getUsername(), ex.getClass().getSimpleName());
        } catch (Exception ex) {
            logger.error("Unexpected error during JWT validation for email={}", userDetails.getUsername(), ex);
        }
        return false;
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}

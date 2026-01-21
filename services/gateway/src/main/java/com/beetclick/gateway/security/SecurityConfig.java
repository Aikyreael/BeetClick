package com.beetclick.gateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    @Order(1)
    public SecurityWebFilterChain publicChain(ServerHttpSecurity http) {
        return http
                .securityMatcher(ServerWebExchangeMatchers.pathMatchers(
                        HttpMethod.POST,
                        "/auth/register",
                        "/auth/login",
                        "/auth/refresh",
                        "/auth/logout"
                ))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex.anyExchange().permitAll())
                .build();
    }

    @Bean
    @Order(2)
    public SecurityWebFilterChain apiChain(ServerHttpSecurity http,
                                           BearerOrCookieTokenConverter tokenConverter,
                                           ReactiveJwtDecoder decoder) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(ex -> ex
                        // Actuator
                        .pathMatchers("/actuator/health").permitAll()

                        // USER
                        .pathMatchers(HttpMethod.GET, "/users/me").hasAnyRole("USER", "ADMIN")
                        .pathMatchers(HttpMethod.GET, "/users/all").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/users/*").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PATCH, "/users/*").hasRole("ADMIN")

                        // BET
                        .pathMatchers("/bets/**").hasAnyRole("USER", "ADMIN")

                        // MATCH
                        .pathMatchers(HttpMethod.GET, "/matches/**").hasAnyRole("USER", "ADMIN")
                        .pathMatchers(HttpMethod.POST, "/matches/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PATCH, "/matches/**").hasRole("ADMIN")

                        // WALLET
                        .pathMatchers(HttpMethod.GET, "/wallets/").hasAnyRole("USER", "ADMIN")
                        .pathMatchers(HttpMethod.GET, "/wallets/all").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/wallets/*").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PATCH, "/wallets/coin").hasRole("ADMIN")

                        // PAYMENT
                        .pathMatchers("/payments/**").hasAnyRole("USER", "ADMIN")

                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .bearerTokenConverter(tokenConverter)
                        .jwt(jwt -> jwt
                                .jwtDecoder(decoder)
                                .jwtAuthenticationConverter(new ReactiveJwtAuthenticationConverterAdapter(this::jwtToAuth))
                        )
                )
                .build();
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        byte[] keyBytes = decodeJwtSecret(jwtSecret);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET must be >= 32 bytes (HS256).");
        }
        SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA256");
        return NimbusReactiveJwtDecoder.withSecretKey(key).build();
    }

    private byte[] decodeJwtSecret(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET is missing/blank");
        }

        try {
            byte[] decoded = Base64.getDecoder().decode(secret);
            if (decoded.length > 0) return decoded;
        } catch (IllegalArgumentException ignored) { }
        return secret.getBytes(StandardCharsets.UTF_8);
    }

    private AbstractAuthenticationToken jwtToAuth(org.springframework.security.oauth2.jwt.Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        if (email == null || email.isBlank()) email = jwt.getSubject();

        Object rawUserId = jwt.getClaims().get("userId");
        if (rawUserId == null) rawUserId = jwt.getClaims().get("user_id");
        String userId = rawUserId != null ? rawUserId.toString() : null;

        String role = jwt.getClaimAsString("role");
        if (role == null || role.isBlank()) role = "USER";

        var authorities = new ArrayList<SimpleGrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        if ("ADMIN".equals(role)) authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(email, userId, authorities);
    }
}

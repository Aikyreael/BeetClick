package com.beetclick.authservice.service;


import com.beetclick.authservice.entity.AuthUser;
import com.beetclick.authservice.exception.EmailAlreadyExistsException;
import com.beetclick.authservice.exception.UnauthorizedException;
import com.beetclick.authservice.exception.UserNotFoundException;
import com.beetclick.authservice.repository.AuthUserRepository;
import com.beetclick.common.dto.auth.request.LoginRequest;
import com.beetclick.common.dto.auth.request.RegisterRequest;
import com.beetclick.common.dto.auth.response.AuthResponse;
import com.beetclick.common.dto.auth.response.RegisterResponse;
import com.beetclick.common.entity.Role;
import com.beetclick.common.event.auth.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

@Service
public class AuthService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AuthUserRepository authUserRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    private final ObjectMapper objectMapper;

    private static final String TOPIC_USER_REGISTERED = "auth.user-registered";

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public AuthService(AuthUserRepository authUserRepository, PasswordEncoder passwordEncoder, JwtService jwtService, RefreshTokenService refreshTokenService, ObjectMapper objectMapper, KafkaTemplate<String, String> kafkaTemplate) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Attempting to load user by email={}", email);
        AuthUser user = authUserRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found with email={}", email);
                    return new UserNotFoundException("Utilisateur non trouvé avec l'email: " + email);
                });

        logger.debug("User loaded successfully: userId={}, email={}", user.getId(), user.getEmail());
        return user;
    }

    public AuthResponse login(LoginRequest request) {
        AuthUser user = authUserRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException("Email ou mot de passe incorrect"));

        if (!user.isEnabled()) {
            throw new UnauthorizedException("Compte non activé. Vérifiez vos emails.");
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Email ou mot de passe incorrect");
        }

        String accessToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.create(user.getId(), user.getRole()).raw();

        return new AuthResponse(user.getId(), user.getEmail(), user.getRole(), accessToken, refreshToken);
    }

    public RegisterResponse register(RegisterRequest req) {
        logger.debug("Attempting to create user with email={}", req.email());

        if (authUserRepository.existsByEmail(req.email())) {
            logger.warn("Email already exists: {}", req.email());
            throw new EmailAlreadyExistsException("Un utilisateur avec cet email existe déjà");
        }

        AuthUser u = new AuthUser();
        u.setEmail(req.email());
        u.setRole(Role.USER);
        u.setPassword_hash(passwordEncoder.encode(req.password()));
        u.setEnabled(true);

        AuthUser saved = authUserRepository.save(u);
        logger.info("User created successfully with id={} and email={}", saved.getId(), saved.getEmail());

        try {
            UserRegisteredEvent event = new UserRegisteredEvent(
                    saved.getId(),
                    saved.getEmail(),
                    Instant.now()
            );

            String payload = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(TOPIC_USER_REGISTERED, saved.getId().toString(), payload)
                    .whenComplete((res, ex) -> {
                        if (ex != null) {
                            logger.warn("Kafka publish failed userId={}", saved.getId(), ex);
                        } else {
                            logger.info("Kafka event sent topic={} userId={}", TOPIC_USER_REGISTERED, saved.getId());
                        }
                    });
        } catch (Exception ex) {
            // On ne casse pas l'inscription si Kafka a un souci (MVP-friendly)
            logger.warn("Kafka publish error ignored userId={}", saved.getId(), ex);
        }

        return convertToResponse(saved);
    }


    public AuthResponse refresh(String rawRefreshToken) {
        var pair = refreshTokenService.rotate(rawRefreshToken);

        AuthUser user = authUserRepository.findById(pair.entity().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.isEnabled()) {
            throw new UnauthorizedException("Compte désactivé");
        }

        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = pair.raw();

        return new AuthResponse(user.getId(), user.getEmail(), user.getRole(), newAccessToken, newRefreshToken);
    }

    private RegisterResponse convertToResponse(AuthUser user) {
        return new RegisterResponse(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }
}

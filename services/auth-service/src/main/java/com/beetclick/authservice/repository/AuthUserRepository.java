package com.beetclick.authservice.repository;

import com.beetclick.authservice.entity.AuthUser;
import com.beetclick.common.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthUserRepository extends JpaRepository<AuthUser, UUID> {
    Optional<AuthUser> findByEmail(String email);
    boolean existsByEmail(String email);
    List<AuthUser> findByRole(Role role);
}

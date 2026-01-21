package com.beetclick.betservice.repository;

import com.beetclick.betservice.entity.Bet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BetRepository extends JpaRepository<Bet, UUID> {
    List<Bet> findByUserId(UUID userId);
    Optional<Bet> findByIdAndUserId(UUID id, UUID userId);
}

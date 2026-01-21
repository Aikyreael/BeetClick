package com.beetclick.matchservice.repository;

import com.beetclick.matchservice.entity.MatchOddsHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MatchOddsHistoryRepository extends JpaRepository<MatchOddsHistory, UUID> {
    List<MatchOddsHistory> findByMatch_IdOrderByCreatedAtDesc(UUID matchId);
}
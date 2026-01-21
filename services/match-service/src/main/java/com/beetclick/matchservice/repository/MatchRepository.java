package com.beetclick.matchservice.repository;

import com.beetclick.matchservice.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface MatchRepository extends JpaRepository<Match, UUID>, JpaSpecificationExecutor<Match> {}

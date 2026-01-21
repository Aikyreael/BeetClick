package com.beetclick.matchservice.entity;

import com.beetclick.common.entity.MatchResult;
import com.beetclick.common.entity.MatchStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "matches")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Match {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "home_team", nullable = false, length = 120)
    private String homeTeam;

    @Column(name = "away_team", nullable = false, length = 120)
    private String awayTeam;

    @Column(name = "kickoff_at", nullable = false)
    private Instant kickoffAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private MatchStatus status = MatchStatus.SCHEDULED;

    @Convert(converter = MatchResultConverter.class)
    @Column(length = 2)
    private MatchResult result;

    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_score")
    private Integer awayScore;

    @Column(name = "odds_home_win", precision = 6, scale = 3)
    private BigDecimal oddsHomeWin;

    @Column(name = "odds_draw", precision = 6, scale = 3)
    private BigDecimal oddsDraw;

    @Column(name = "odds_away_win", precision = 6, scale = 3)
    private BigDecimal oddsAwayWin;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private int version;

    @PrePersist
    void onCreate() {
        var now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

}
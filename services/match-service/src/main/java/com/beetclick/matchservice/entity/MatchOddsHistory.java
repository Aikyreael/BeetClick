package com.beetclick.matchservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "match_odds_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchOddsHistory {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Column(name = "odds_home_win", precision = 6, scale = 3)
    private BigDecimal oddsHomeWin;

    @Column(name = "odds_draw", precision = 6, scale = 3)
    private BigDecimal oddsDraw;

    @Column(name = "odds_away_win", precision = 6, scale = 3)
    private BigDecimal oddsAwayWin;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Version
    private int version;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

}

package com.beetclick.matchservice.service;

import com.beetclick.common.dto.match.request.*;
import com.beetclick.common.dto.match.response.MatchResponse;
import com.beetclick.common.dto.match.response.OddsHistoryResponse;
import com.beetclick.common.entity.MatchStatus;
import com.beetclick.common.event.match.MatchEvent;
import com.beetclick.common.event.match.MatchFinishedEvent;
import com.beetclick.matchservice.entity.Match;
import com.beetclick.matchservice.entity.MatchOddsHistory;
import com.beetclick.matchservice.kafka.KafkaPublisher;
import com.beetclick.matchservice.repository.MatchOddsHistoryRepository;
import com.beetclick.matchservice.repository.MatchRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class MatchService {

    public static final String TOPIC_MATCH_EVENTS = "match.events";
    public static final String TOPIC_MATCH_FINISHED = "match.finished";

    private final MatchRepository matchRepository;
    private final MatchOddsHistoryRepository oddsHistoryRepository;
    private final KafkaPublisher kafkaPublisher;

    public MatchService(MatchRepository matchRepository,
                        MatchOddsHistoryRepository oddsHistoryRepository,
                        KafkaPublisher kafkaPublisher) {
        this.matchRepository = matchRepository;
        this.oddsHistoryRepository = oddsHistoryRepository;
        this.kafkaPublisher = kafkaPublisher;
    }

    @Transactional
    public MatchResponse create(MatchCreateRequest req) {
        Match m = new Match();
        m.setHomeTeam(req.homeTeam());
        m.setAwayTeam(req.awayTeam());
        m.setKickoffAt(req.kickoffAt());
        m.setStatus(MatchStatus.SCHEDULED);
        m.setOddsHomeWin(req.oddsHomeWin());
        m.setOddsDraw(req.oddsDraw());
        m.setOddsAwayWin(req.oddsAwayWin());

        Match saved = matchRepository.save(m);

        saveOddsSnapshot(saved);
        kafkaPublisher.publish(TOPIC_MATCH_EVENTS, saved.getId(), new MatchEvent("MATCH_CREATED", saved.getId(), Instant.now()));

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public MatchResponse get(UUID id) throws ChangeSetPersister.NotFoundException {
        return toResponse(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<MatchResponse> list(String status, Instant from, Instant to, String team, Pageable pageable) {
        Specification<Match> spec = (root, query, cb) -> {
            var predicates = new java.util.ArrayList<Predicate>();

            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), MatchStatus.valueOf(status)));
            }
            if (from != null) predicates.add(cb.greaterThanOrEqualTo(root.get("kickoffAt"), from));
            if (to != null) predicates.add(cb.lessThanOrEqualTo(root.get("kickoffAt"), to));

            if (team != null && !team.isBlank()) {
                var like = "%" + team.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("homeTeam")), like),
                        cb.like(cb.lower(root.get("awayTeam")), like)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return matchRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Transactional
    public MatchResponse update(UUID id, MatchUpdateRequest req) throws ChangeSetPersister.NotFoundException {
        Match m = findOrThrow(id);

        if (req.homeTeam() != null) m.setHomeTeam(req.homeTeam());
        if (req.awayTeam() != null) m.setAwayTeam(req.awayTeam());
        if (req.kickoffAt() != null) m.setKickoffAt(req.kickoffAt());

        if (req.oddsHomeWin() != null) m.setOddsHomeWin(req.oddsHomeWin());
        if (req.oddsDraw() != null) m.setOddsDraw(req.oddsDraw());
        if (req.oddsAwayWin() != null) m.setOddsAwayWin(req.oddsAwayWin());

        Match saved = matchRepository.save(m);

        if (req.oddsHomeWin() != null || req.oddsDraw() != null || req.oddsAwayWin() != null) {
            saveOddsSnapshot(saved);
        }

        kafkaPublisher.publish(TOPIC_MATCH_EVENTS, saved.getId(), new MatchEvent("MATCH_UPDATED", saved.getId(), Instant.now()));
        return toResponse(saved);
    }

    @Transactional
    public MatchResponse updateOdds(UUID id, MatchOddsUpdateRequest req) throws ChangeSetPersister.NotFoundException {
        Match m = findOrThrow(id);
        m.setOddsHomeWin(req.oddsHomeWin());
        m.setOddsDraw(req.oddsDraw());
        m.setOddsAwayWin(req.oddsAwayWin());

        Match saved = matchRepository.save(m);
        saveOddsSnapshot(saved);

        kafkaPublisher.publish(TOPIC_MATCH_EVENTS, saved.getId(), new MatchEvent("MATCH_ODDS_UPDATED", saved.getId(), Instant.now()));
        return toResponse(saved);
    }

    @Transactional
    public MatchResponse updateStatus(UUID id, MatchStatusUpdateRequest req) throws ChangeSetPersister.NotFoundException {
        Match m = findOrThrow(id);
        m.setStatus(req.status());
        Match saved = matchRepository.save(m);

        kafkaPublisher.publish(TOPIC_MATCH_EVENTS, saved.getId(), new MatchEvent("MATCH_STATUS_UPDATED", saved.getId(), Instant.now()));
        return toResponse(saved);
    }

    @Transactional
    public MatchResponse updateResult(UUID id, MatchResultUpdateRequest req) throws ChangeSetPersister.NotFoundException {
        Match m = findOrThrow(id);
        m.setResult(req.result());
        m.setHomeScore(req.homeScore());
        m.setAwayScore(req.awayScore());
        m.setStatus(MatchStatus.FINISHED);

        Match saved = matchRepository.save(m);

        kafkaPublisher.publish(TOPIC_MATCH_FINISHED, saved.getId(), new MatchFinishedEvent(saved.getId(), saved.getResult()));
        return toResponse(saved);
    }

    @Transactional
    public MatchResponse cancel(UUID id) throws ChangeSetPersister.NotFoundException {
        Match m = findOrThrow(id);
        m.setStatus(MatchStatus.CANCELLED);
        Match saved = matchRepository.save(m);

        kafkaPublisher.publish(TOPIC_MATCH_EVENTS, saved.getId(), new MatchEvent("MATCH_CANCELLED", saved.getId(), Instant.now()));
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<OddsHistoryResponse> oddsHistory(UUID id) throws ChangeSetPersister.NotFoundException {
        findOrThrow(id);

        return oddsHistoryRepository.findByMatch_IdOrderByCreatedAtDesc(id).stream()
                .map(h -> new OddsHistoryResponse(
                        h.getId(),
                        id,
                        h.getOddsHomeWin(),
                        h.getOddsDraw(),
                        h.getOddsAwayWin(),
                        h.getCreatedAt()
                ))
                .toList();
    }

    private Match findOrThrow(UUID id) throws ChangeSetPersister.NotFoundException {
        return matchRepository.findById(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
    }

    private void saveOddsSnapshot(Match match) {
        MatchOddsHistory h = new MatchOddsHistory();
        h.setMatch(match);
        h.setOddsHomeWin(match.getOddsHomeWin());
        h.setOddsDraw(match.getOddsDraw());
        h.setOddsAwayWin(match.getOddsAwayWin());
        oddsHistoryRepository.save(h);
    }

    private MatchResponse toResponse(Match m) {
        return new MatchResponse(
                m.getId(),
                m.getHomeTeam(),
                m.getAwayTeam(),
                m.getKickoffAt(),
                m.getStatus(),
                m.getResult(),
                m.getHomeScore(),
                m.getAwayScore(),
                m.getOddsHomeWin(),
                m.getOddsDraw(),
                m.getOddsAwayWin(),
                m.getCreatedAt(),
                m.getUpdatedAt(),
                m.getVersion()
        );
    }
}
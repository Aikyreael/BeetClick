package com.beetclick.betservice.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.beetclick.betservice.client.MatchClient;
import com.beetclick.betservice.client.WalletClient;
import com.beetclick.betservice.dto.request.BetCreateRequest;
import com.beetclick.betservice.dto.response.BetCreatedResponse;
import com.beetclick.betservice.entity.Bet;
import com.beetclick.betservice.entity.BetOption;
import com.beetclick.betservice.producer.BetEventProducer;
import com.beetclick.betservice.repository.BetRepository;

import jakarta.transaction.Transactional;

@Service
public class BetService {
    private final BetRepository betRepository;
    private final MatchClient matchClient;
    private final WalletClient walletClient;
    private final BetEventProducer eventProducer;

    public BetService(BetRepository betRepository, MatchClient matchClient, WalletClient walletClient, BetEventProducer eventProducer) {
        this.betRepository = betRepository;
        this.matchClient = matchClient;
        this.walletClient = walletClient;
        this.eventProducer = eventProducer;
    }

    public List<BetCreatedResponse> getUserBets() {
        UUID userId = getCurrentUserId();
        return betRepository.findByUserId(userId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public BetCreatedResponse getUserBetById(UUID id) {
        UUID userId = getCurrentUserId();
        Bet bet = betRepository.findByIdAndUserId(id, userId)
            .orElseThrow();
        return toResponse(bet);
    }

    public List<BetCreatedResponse> getAllBets() {
        return betRepository.findAll()
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public BetCreatedResponse getBetById(UUID id) {
        return betRepository.findById(id)
            .map(this::toResponse)
            .orElseThrow();
    }

    @Transactional
    public BetCreatedResponse createBet(BetCreateRequest req) {
        UUID userId = getCurrentUserId();

        var match = matchClient.getMatch(request.getMatchId());
        var wallet = walletClient.getWallet(userId);

        if (!"SCHEDULED".equals(match.status())) {
            throw new IllegalStateException("Ce match n'est pas ouvert aux paris");
        }

        if (wallet.balance() < request.getAmount()) {
            throw new IllegalStateException("Vous n'avez pas assez de fonds dans votre portefeuille");
        }

        // Calculer le gain potentiel
        double odds = switch (req.getOption().toLowerCase()) {
            case "1" -> match.getOddsTeam1();
            case "x" -> match.getOddsDraw();
            case "2" -> match.getOddsTeam2();
            default -> throw new IllegalArgumentException("Option non valide");
        };
        double gain = req.getAmount() * odds;

        // Créer et sauvegarder le pari
        Bet bet = new Bet();
        bet.setUserId(userId);
        bet.setMatchId(req.getMatchId());
        bet.setAmount(req.getAmount());
        bet.setOdds(odds);
        bet.setGain(gain);
        bet.setOption(BetOption.valueOf(mapOption(req.getOption())));
        bet.setCreatedBy(userId);
        Bet savedBet = betRepository.save(bet);

        // Publier l'événement de création de pari
        eventProducer.publishBetPlaced(savedBet);

        return toResponse(savedBet);
    }

    private BetCreatedResponse toResponse(Bet bet) {
            BetCreatedResponse res = new BetCreatedResponse();
            res.setId(bet.getId());
            res.setUserId(bet.getUserId());
            res.setMatchId(bet.getMatchId());
            res.setAmount(bet.getAmount());
            res.setOdds(bet.getOdds());
            res.setGain(bet.getGain());
            res.setOption(bet.getOption().name());
            return res;
    }

    private UUID getCurrentUserId() {
        //TODO: Remplacer par la récupération par JWT ou service User
        return UUID.randomUUID();
    }

    private String mapOption(String option) {
        switch (option.toLowerCase()) {
            case "1" -> BetOption._1.name();
            case "X" -> BetOption.X.name();
            case "2" -> BetOption._2.name();
            default -> throw new IllegalArgumentException("Invalid option");
        }
    }
}

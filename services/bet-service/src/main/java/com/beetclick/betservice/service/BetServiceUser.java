package com.beetclick.betservice.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.beetclick.betservice.client.MatchClient;
import com.beetclick.betservice.client.MatchClient.MatchResponse;
import com.beetclick.betservice.client.WalletClient;
import com.beetclick.betservice.dto.request.BetCreateRequest;
import com.beetclick.betservice.dto.response.BetCreatedResponse;
import com.beetclick.betservice.entity.Bet;
import com.beetclick.betservice.entity.BetOption;
import com.beetclick.betservice.event.BetPlaced;
import com.beetclick.betservice.producer.BetEventProducer;
import com.beetclick.betservice.repository.BetRepository;
import com.beetclick.betservice.utils.BetMapper;

import jakarta.transaction.Transactional;

@Service
public class BetServiceUser {

    private final BetRepository betRepository;
    private final BetMapper betMapper;
    private final MatchClient matchClient;
    private final WalletClient walletClient;
    private final BetEventProducer eventProducer;

    public BetServiceUser(BetRepository betRepository, BetMapper betMapper, MatchClient matchClient, WalletClient walletClient, BetEventProducer eventProducer) {
        this.betRepository = betRepository;
        this.betMapper = betMapper;
        this.matchClient = matchClient;
        this.walletClient = walletClient;
        this.eventProducer = eventProducer;
    }

    public List<BetCreatedResponse> getUserBets(UUID userId) {
        return betRepository.findByUserId(userId)
            .stream()
            .map(betMapper::toResponse)
            .collect(Collectors.toList());
    }

    public BetCreatedResponse getUserBetById(UUID userId, UUID id) {
        Bet bet = betRepository.findByIdAndUserId(id, userId)
            .orElseThrow();
        return betMapper.toResponse(bet);
    }

    @Transactional
    public BetCreatedResponse createBet(UUID userId, BetCreateRequest req) {
        MatchResponse match = matchClient.getMatch(req.getMatchId());
        double balance = walletClient.getBalance(userId);

        // Un match doit être SCHEDULED pour accepter les paris
        if (!"SCHEDULED".equals(match.status())) {
            throw new IllegalStateException("Ce match n'est pas ouvert aux paris");
        }

        // Un utilisateur doit avoir assez de fonds dans son portefeuille pour parier
        if (balance < req.getAmount()) {
            throw new IllegalStateException("Vous n'avez pas assez de fonds dans votre portefeuille");
        }

        // Calcul du gain potentiel
        double odds = switch (req.getOption().toLowerCase()) {
            case "1" -> match.oddsTeam1();
            case "x" -> match.oddsDraw();
            case "2" -> match.oddsTeam2();
            default -> throw new IllegalArgumentException("Option non valide");
        };
        double gain = req.getAmount() * odds;

        // Création du pari
        Bet bet = new Bet();
        bet.setUserId(userId);
        bet.setMatchId(req.getMatchId());
        bet.setAmount(req.getAmount());
        bet.setOdds(odds);
        bet.setGain(gain);
        bet.setOption(BetOption.valueOf(mapOption(req.getOption())));
        bet.setCreatedBy(userId);
        Bet savedBet = betRepository.save(bet);

        // Publication de l'évenement pour le débit du portefeuille de l'utilisateur
        eventProducer.publishBetPlaced(new BetPlaced(bet.getUserId(), bet.getMatchId(), bet.getAmount()));

        return betMapper.toResponse(savedBet);
    }

    private String mapOption(String option) {
        return switch (option.toLowerCase()) {
            case "1" -> BetOption._1.name();
            case "x" -> BetOption.X.name();
            case "2" -> BetOption._2.name();
            default -> throw new IllegalArgumentException("Invalid option");
        };
    }
}

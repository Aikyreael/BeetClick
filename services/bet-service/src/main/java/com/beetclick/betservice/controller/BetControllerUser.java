package com.beetclick.betservice.controller;

import com.beetclick.betservice.dto.request.BetCreateRequest;
import com.beetclick.betservice.dto.response.BetCreatedResponse;
import com.beetclick.betservice.service.BetServiceUser;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bets")
public class BetControllerUser {
    private final BetServiceUser betServiceUser;

    public BetControllerUser(BetServiceUser betService) {
        this.betServiceUser = betService;
    }

    @GetMapping("/all")
    public List<BetCreatedResponse> getUserBets(@RequestHeader("X-User-Id") UUID userId) {
        return betServiceUser.getUserBets(userId);
    }

    @GetMapping("/{id}")
    public BetCreatedResponse getUserBetById(@RequestHeader("X-User-Id") UUID userId, @PathVariable UUID id) {
        return betServiceUser.getUserBetById(userId, id);
    }

    @PostMapping
    public BetCreatedResponse createBet(@RequestHeader("X-User-Id") UUID userId, @RequestBody BetCreateRequest req) {
        return betServiceUser.createBet(userId, req);
    }
}

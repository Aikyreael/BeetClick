package com.beetclick.betservice.controller;

import com.beetclick.betservice.dto.request.BetCreateRequest;
import com.beetclick.betservice.dto.response.BetCreatedResponse;
import com.beetclick.betservice.service.BetService;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bets")
public class BetController {
    private final BetService betService;

    public BetController(BetService betService) {
        this.betService = betService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('USER')")
    public List<BetCreatedResponse> getUserBets() {
        return betService.getUserBets();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public BetCreatedResponse getBetById(@PathVariable UUID id) {
        return betService.getBetById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public BetCreatedResponse createBet(@RequestBody BetCreateRequest req) {
        return betService.createBet(req);
    }
}

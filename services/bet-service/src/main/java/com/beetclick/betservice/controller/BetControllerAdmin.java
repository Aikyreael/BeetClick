package com.beetclick.betservice.controller;

import com.beetclick.betservice.dto.response.BetCreatedResponse;
import com.beetclick.betservice.service.BetServiceAdmin;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/bets")
public class BetControllerAdmin {
    private final BetServiceAdmin betServiceAdmin;

    public BetControllerAdmin(BetServiceAdmin betServiceAdmin) {
        this.betServiceAdmin = betServiceAdmin;
    }

    @GetMapping("/all")
    public List<BetCreatedResponse> getAllBets() {
        return betServiceAdmin.getAllBets();
    }

    @GetMapping("/{id}")
    public BetCreatedResponse getBetById(@PathVariable UUID id) {
        return betServiceAdmin.getBetById(id);
    }
}

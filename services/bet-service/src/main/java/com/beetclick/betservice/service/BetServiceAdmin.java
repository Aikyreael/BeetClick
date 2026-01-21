package com.beetclick.betservice.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.beetclick.betservice.dto.response.BetCreatedResponse;
import com.beetclick.betservice.repository.BetRepository;
import com.beetclick.betservice.utils.BetMapper;


@Service
public class BetServiceAdmin {
    private final BetRepository betRepository;
    private final BetMapper betMapper;

    public BetServiceAdmin(BetRepository betRepository, BetMapper betMapper) {
        this.betRepository = betRepository;
        this.betMapper = betMapper;
    }

    public List<BetCreatedResponse> getAllBets() {
        return betRepository.findAll()
            .stream()
            .map(betMapper::toResponse)
            .collect(Collectors.toList());
    }

    public BetCreatedResponse getBetById(UUID id) {
        return betRepository.findById(id)
            .map(betMapper::toResponse)
            .orElseThrow();
    }
}

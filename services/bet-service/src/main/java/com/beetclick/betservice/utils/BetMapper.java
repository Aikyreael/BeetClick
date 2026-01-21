package com.beetclick.betservice.utils;

import org.springframework.stereotype.Component;

import com.beetclick.betservice.dto.response.BetCreatedResponse;
import com.beetclick.betservice.entity.Bet;

@Component
public class BetMapper {
    public BetCreatedResponse toResponse(Bet bet) {
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
}

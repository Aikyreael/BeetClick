package com.beetclick.walletservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class WalletResponse {
    private BigDecimal balance;
    private int coin;

    public WalletResponse(BigDecimal balance,int coin) {
        this.coin = coin;
        this.balance = balance;
    }
}


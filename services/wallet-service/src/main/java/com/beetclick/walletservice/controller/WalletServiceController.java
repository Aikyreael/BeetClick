package com.beetclick.walletservice.controller;
import com.beetclick.walletservice.dto.WalletResponse;
import org.springframework.web.bind.annotation.*;
import com.beetclick.walletservice.service.WalletService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/wallets")
public class WalletServiceController {
private final WalletService walletService;

    public WalletServiceController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/{userId}")
    public WalletResponse getMyWallet(@PathVariable UUID userId) {
        WalletResponse wallet = walletService.getWalletByUserId(userId);
        return wallet;
    }
}

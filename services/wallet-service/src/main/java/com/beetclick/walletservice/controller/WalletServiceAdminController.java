package com.beetclick.walletservice.controller;

import com.beetclick.walletservice.dto.WalletResponse;
import com.beetclick.walletservice.service.WalletService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/wallets")
public class WalletServiceAdminController {
    private final WalletService walletService;

    public WalletServiceAdminController(WalletService walletService) {
        this.walletService = walletService;
    }

    //Route Admin
    @GetMapping("/all")
    public List<WalletResponse> getAllWallets(){
        List<WalletResponse> walletList = walletService.getAllWallets();
        return walletList;
    }

    //Route Admin
    @GetMapping("/{walletId}")
    public WalletResponse getWalletById(@PathVariable UUID walletId) {
        WalletResponse wallet = walletService.getWalletByWalletId(walletId);
        return wallet;
    }

    //Route Admin
    @PatchMapping("/{walletId}")
    public WalletResponse walletCoinsRequest(@PathVariable UUID walletId, @RequestParam int coin) {
        WalletResponse updatedWallet = walletService.updateWallet(walletId, coin);
        WalletResponse response = new WalletResponse(
                updatedWallet.getBalance(),
                updatedWallet.getCoin()
        );
        return response;
    }
}

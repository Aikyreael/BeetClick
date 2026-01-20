package controller;

import dto.WalletResponse;
import entity.Wallet;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.WalletService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/wallets")
public class WalletServiceController {
private final WalletService walletService;

    public WalletServiceController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping()
    public WalletResponse getMyWallet(@RequestParam UUID userId) {

        WalletResponse wallet = walletService.getWalletByUserId(userId);

        return wallet;
    }

    //Route Admin
    @GetMapping("/all")
    public List<WalletResponse> getAllWallets(){

        List<WalletResponse> walletList = walletService.getAllWallets();

        return walletList;
    }

    //Route Admin
    @GetMapping("/{walletId}")
    public WalletResponse getWalletById(@RequestParam UUID userId) {

        WalletResponse wallet = walletService.getWalletByUserId(userId);

        return wallet;
    }

    //Route Admin
    @PatchMapping("/{walletId}")
    public WalletResponse walletCoinsRequest(
            @RequestParam UUID walletId,
            @RequestParam int coin) {

        WalletResponse updatedWallet = walletService.updateWallet(walletId, coin);

        WalletResponse response = new WalletResponse(
                updatedWallet.getBalance(),
                updatedWallet.getCoin()
        );

        return ResponseEntity.ok(response);
    }

}

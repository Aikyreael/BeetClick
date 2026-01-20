package service;

import dto.WalletResponse;
import entity.Wallet;
import jakarta.persistence.EntityNotFoundException;
import repository.WalletRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public List<WalletResponse> getAllWallets() {

        return walletRepository.findAll();

    }

    public WalletResponse getWalletByUserId(UUID userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Wallet not found for userId=" + userId
                        )
                );
    }

    public WalletResponse getWalletByWalletId(UUID walletId){
        return walletRepository.findById(walletId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Wallet not found for walletId=" + walletId
                        )
                );
    }

    public WalletResponse updateWallet(UUID walletId, int coin) {

        WalletResponse wallet = walletRepository.findById(walletId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Wallet not found for walletId=" + walletId
                        )
                );

        wallet.setCoin(coin);
        WalletResponse updatedWallet = walletRepository.save(wallet);

        return new WalletResponse(
                updatedWallet.getBalance(),
                updatedWallet.getCoin()
        );
    }


    public void creditWallet(UUID walletId, int amount) {
        WalletResponse wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setBalance(wallet.getBalance().add(BigDecimal.valueOf(amount)));
        walletRepository.save(wallet);
    }

    public void debitWallet(UUID walletId, int amount) {
        WalletResponse wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        wallet.setBalance(wallet.getBalance().subtract(BigDecimal.valueOf(amount)));
        walletRepository.save(wallet);
    }
}

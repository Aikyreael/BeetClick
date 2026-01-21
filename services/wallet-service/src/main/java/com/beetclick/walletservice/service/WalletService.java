package com.beetclick.walletservice.service;

import com.beetclick.walletservice.dto.WalletResponse;
import com.beetclick.walletservice.entity.Wallet;
import jakarta.persistence.EntityNotFoundException;
import com.beetclick.walletservice.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public List<WalletResponse> getAllWallets() {
        List<Wallet> walletList = walletRepository.findAll();
        return walletList.stream()
                .map(wallet -> new WalletResponse(
                        wallet.getBalance(),
                        wallet.getCoin()
                ))
                .toList();
    }

    public WalletResponse getWalletByUserId(UUID userId) {
        Wallet wallet  =  walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for userId: " + userId));

        return  new WalletResponse(
                        wallet.getBalance(),
                        wallet.getCoin()
                );
    }

    public WalletResponse getWalletByWalletId(UUID walletId){
        Wallet wallet =  walletRepository.findById(walletId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Wallet not found for walletId=" + walletId));

        return new WalletResponse(
                wallet.getBalance(),
                wallet.getCoin()
        );
    }

    public WalletResponse updateWallet(UUID walletId, int coin) {

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Wallet not found for walletId=" + walletId
                        )
                );

        wallet.setCoin(coin);
        Wallet updatedWallet = walletRepository.save(wallet);

        return new WalletResponse(
                updatedWallet.getBalance(),
                updatedWallet.getCoin()
        );
    }


    public void creditWallet(UUID walletId, int amount) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setBalance(wallet.getBalance().add(BigDecimal.valueOf(amount)));
        walletRepository.save(wallet);
    }

    public void debitWallet(UUID walletId, int amount) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        wallet.setBalance(wallet.getBalance().subtract(BigDecimal.valueOf(amount)));
        walletRepository.save(wallet);
    }
}

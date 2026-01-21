package com.beetclick.walletservice.repository;

import com.beetclick.walletservice.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    Optional<Wallet> findByUserId(UUID userId);

    boolean existsByUserId(UUID uuid);

    @Override
    List<Wallet> findAll();

    @Override
    Optional<Wallet> findById(UUID uuid);

    @Override
     Wallet save( Wallet wallet);


}


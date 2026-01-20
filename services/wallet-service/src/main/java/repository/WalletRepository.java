package repository;

import dto.WalletResponse;
import entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<WalletResponse, UUID> {

    Optional<WalletResponse> findByUserId(UUID userId);

    @Override
    List<WalletResponse> findAll();

    @Override
    Optional<WalletResponse> findById(UUID uuid);

    @Override
     WalletResponse save( WalletResponse walletResponse);
}


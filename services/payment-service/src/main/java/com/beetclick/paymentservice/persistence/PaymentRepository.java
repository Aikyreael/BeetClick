package com.beetclick.paymentservice.persistence;

import com.beetclick.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Override
    List<Payment> findAll();

    Optional<Payment> findById(UUID userId);

    @Query("""
        SELECT COALESCE(SUM(p.amount), 0)
        FROM Payment p
        WHERE p.walletId = :walletId
        AND p.status = 'SUCCESS'
        AND p.createdAt >= :startOfDay
    """)
    double sumTodayCredits(UUID walletId, LocalDateTime startOfDay);

    Payment save(Payment payment);

}
package com.beetclick.paymentservice.entity;

import com.beetclick.common.entity.PaymentCategory;
import com.beetclick.common.entity.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue
    private UUID id;

    private double amount;

    private UUID userId;
    private UUID walletId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentCategory category;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(updatable = false)
    private UUID createdBy;

    public Payment(double amount, PaymentCategory category, PaymentStatus status) {
        this.amount = amount;
        this.category = category;
        this.status = status;
    }
}

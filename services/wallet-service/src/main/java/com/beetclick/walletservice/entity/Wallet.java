package com.beetclick.walletservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "wallet")
@Getter
@Setter
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID userId;

    private BigDecimal balance;
    private int coin;
    private Date createdAt;
    private Date updated_at;
    private String createdBy;
    private String updatedBy;
}

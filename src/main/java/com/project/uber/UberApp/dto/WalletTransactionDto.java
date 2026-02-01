package com.project.uber.UberApp.dto;

import com.project.uber.UberApp.entities.enums.TransactionMethod;
import com.project.uber.UberApp.entities.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletTransactionDto {

    private Long id;

    private Double amount;

    private TransactionType transactionType;

    private TransactionMethod transactionMethod;

    private RideDto ride;
    private String transactionId;

    private LocalDateTime timeStamp;

    private WalletDto wallet;
}

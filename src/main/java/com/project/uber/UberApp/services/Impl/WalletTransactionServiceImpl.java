package com.project.uber.UberApp.services.Impl;

import com.project.uber.UberApp.entities.WalletTransaction;
import com.project.uber.UberApp.repository.WalletTransactionRepository;
import com.project.uber.UberApp.services.WalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletTransactionServiceImpl implements WalletTransactionService {

    private final ModelMapper modelMapper;
    private final WalletTransactionRepository walletTransactionRepository;

    @Override
    public void createNewWalletTransaction(WalletTransaction walletTransaction) {
        walletTransactionRepository.save(walletTransaction);
    }
}

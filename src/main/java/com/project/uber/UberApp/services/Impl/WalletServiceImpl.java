package com.project.uber.UberApp.services.Impl;

import com.project.uber.UberApp.entities.Ride;
import com.project.uber.UberApp.entities.User;
import com.project.uber.UberApp.entities.Wallet;
import com.project.uber.UberApp.entities.WalletTransaction;
import com.project.uber.UberApp.entities.enums.TransactionMethod;
import com.project.uber.UberApp.entities.enums.TransactionType;
import com.project.uber.UberApp.exception.ResourceNotFoundException;
import com.project.uber.UberApp.repository.WalletRepository;
import com.project.uber.UberApp.services.WalletService;
import com.project.uber.UberApp.services.WalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final ModelMapper modelMapper;
    private final WalletTransactionService walletTransactionService;

    @Override
    @Transactional
    public Wallet addMoneyToWallet(User user, Double amount, String transactionId, Ride ride,
                                   TransactionMethod transactionMethod) {
        Wallet wallet = findByUser(user);
        wallet.setBalance(wallet.getBalance() + amount);

        WalletTransaction walletTransaction = WalletTransaction
                .builder()
                .transactionId(transactionId)
                .ride(ride)
                .transactionMethod(transactionMethod)
                .wallet(wallet)
                .transactionType(TransactionType.CREDIT)
                .amount(amount)
                .build();

        walletTransactionService.createNewWalletTransaction(walletTransaction);
        return walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public Wallet deductMoneyFromWallet(User user, Double amount, String transactionId, Ride ride,
                                        TransactionMethod transactionMethod) {
        Wallet wallet = findByUser(user);
        wallet.setBalance(wallet.getBalance() - amount);

        WalletTransaction walletTransaction = WalletTransaction
                .builder()
                .transactionId(transactionId)
                .ride(ride)
                .transactionMethod(transactionMethod)
                .wallet(wallet)
                .transactionType(TransactionType.DEBIT)
                .amount(amount)
                .build();

//        walletTransactionService.createNewWalletTransaction(walletTransaction);

        wallet.getTransactions().add(walletTransaction);

        return walletRepository.save(wallet);
    }

    @Override
    public void withdrawAllMyMoneyFromWallet() {

    }

    @Override
    public Wallet findWalletById(Long walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id "+walletId));
    }

    @Override
    public Wallet createNewWallet(User user) {
        Wallet wallet = new Wallet();
        wallet.setUser(user);

        return walletRepository.save(wallet);
    }

    @Override
    public Wallet findByUser(User user) {
        return walletRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user with id "+user.getId()));
    }
}

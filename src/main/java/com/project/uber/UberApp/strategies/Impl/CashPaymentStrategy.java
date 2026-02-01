package com.project.uber.UberApp.strategies.Impl;

import com.project.uber.UberApp.entities.Driver;
import com.project.uber.UberApp.entities.Payment;
import com.project.uber.UberApp.entities.enums.PaymentStatus;
import com.project.uber.UberApp.entities.enums.TransactionMethod;
import com.project.uber.UberApp.repository.PaymentRepository;
import com.project.uber.UberApp.services.WalletService;
import com.project.uber.UberApp.strategies.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CashPaymentStrategy implements PaymentStrategy {

    private final WalletService walletService;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public void processPayment(Payment payment) {
        Driver driver = payment.getRide().getDriver();

        double platformCommission = payment.getAmount() * PLATFORM_COMMISSION;
        walletService.deductMoneyFromWallet(driver.getUser(), platformCommission, null,
                payment.getRide(), TransactionMethod.RIDE);

        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        paymentRepository.save(payment);
    }
}

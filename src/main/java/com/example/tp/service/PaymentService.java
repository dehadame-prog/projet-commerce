package com.example.tp.service;

import com.example.tp.model.Payment;
import com.example.tp.model.User;
import com.example.tp.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Payment createPayment(User user, Payment payment, Double amount, String itemsSummary, String recipientSummary) {
        payment.setUser(user);
        payment.setAmount(amount);
        payment.setItemsSummary(itemsSummary);
        payment.setRecipientSummary(recipientSummary);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setStatus("PAYÉ");
        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsForUser(User user) {
        return paymentRepository.findByUserOrderByCreatedAtDesc(user);
    }
}

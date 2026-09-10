package com.triphub.payment.service;

import com.triphub.payment.domain.Payment;
import com.triphub.payment.dto.CreatePaymentRequest;
import com.triphub.payment.dto.PaymentResponse;
import com.triphub.payment.repository.PaymentRepository;
import com.triphub.shared.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentResponse createPayment(CreatePaymentRequest request) {
        Payment payment = new Payment();
        payment.setBookingId(request.getBookingId());
        payment.setUserId(request.getUserId());
        payment.setAmount(request.getAmount());
        payment.setCurrency("INR");
        payment.setStatus("CREATED");

        Payment saved = paymentRepository.save(payment);
        return new PaymentResponse(
                saved.getId(),
                saved.getBookingId(),
                saved.getUserId(),
                saved.getAmount(),
                saved.getCurrency(),
                saved.getStatus()
        );
    }

    public PaymentResponse processWebhook(String bookingId) {
        Payment payment = paymentRepository.findAll().stream()
                .filter(p -> p.getBookingId().equals(bookingId))
                .findFirst()
                .orElseThrow(() -> new AppException("Payment not found", 404));

        payment.setStatus("PAID");
        paymentRepository.save(payment);

        return new PaymentResponse(
                payment.getId(),
                payment.getBookingId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus()
        );
    }
}

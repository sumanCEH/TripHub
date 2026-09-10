package com.triphub.payment.controller;

import com.triphub.payment.dto.CreatePaymentRequest;
import com.triphub.payment.dto.PaymentResponse;
import com.triphub.payment.service.PaymentService;
import com.triphub.shared.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.createPayment(request), "Payment created successfully"));
    }

    @PostMapping("/webhook/{bookingId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> webhook(@PathVariable String bookingId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.processWebhook(bookingId), "Payment processed successfully"));
    }
}

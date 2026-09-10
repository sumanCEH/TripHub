package com.triphub.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String id;
    private String bookingId;
    private String userId;
    private double amount;
    private String currency;
    private String status;
}

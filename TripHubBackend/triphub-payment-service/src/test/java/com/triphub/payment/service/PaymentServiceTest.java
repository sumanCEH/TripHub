package com.triphub.payment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.triphub.payment.domain.Payment;
import com.triphub.payment.dto.CreatePaymentRequest;
import com.triphub.payment.dto.PaymentResponse;
import com.triphub.payment.repository.PaymentRepository;
import com.triphub.shared.exception.AppException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(paymentRepository);
    }

    @Test
    @DisplayName("Should create payment with all required fields")
    void testCreatePayment() {
        // Arrange
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setBookingId("booking-123");
        request.setUserId("user-456");
        request.setAmount(999.99);

        Payment savedPayment = new Payment();
        savedPayment.setId("payment-789");
        savedPayment.setBookingId("booking-123");
        savedPayment.setUserId("user-456");
        savedPayment.setAmount(999.99);
        savedPayment.setCurrency("INR");
        savedPayment.setStatus("CREATED");

        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        // Act
        PaymentResponse response = paymentService.createPayment(request);

        // Assert
        assertNotNull(response);
        assertEquals("payment-789", response.getId());
        assertEquals("booking-123", response.getBookingId());
        assertEquals("user-456", response.getUserId());
        assertEquals(999.99, response.getAmount());
        assertEquals("INR", response.getCurrency());
        assertEquals("CREATED", response.getStatus());
    }

    @Test
    @DisplayName("Should set currency to INR for created payment")
    void testCreatePaymentSetsCurrencyToINR() {
        // Arrange
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setBookingId("bk-1");
        request.setUserId("u-1");
        request.setAmount(500.00);

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);

        Payment savedPayment = new Payment();
        savedPayment.setCurrency("INR");

        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        // Act
        paymentService.createPayment(request);

        // Assert
        verify(paymentRepository).save(captor.capture());
        assertEquals("INR", captor.getValue().getCurrency());
    }

    @Test
    @DisplayName("Should set initial status to CREATED")
    void testCreatePaymentInitialStatus() {
        // Arrange
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setBookingId("bk-2");
        request.setUserId("u-2");
        request.setAmount(1000.00);

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);

        Payment savedPayment = new Payment();
        savedPayment.setStatus("CREATED");

        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        // Act
        paymentService.createPayment(request);

        // Assert
        verify(paymentRepository).save(captor.capture());
        assertEquals("CREATED", captor.getValue().getStatus());
    }

    @Test
    @DisplayName("Should process webhook and update payment status to PAID")
    void testProcessWebhook() {
        // Arrange
        Payment payment = new Payment();
        payment.setId("payment-111");
        payment.setBookingId("booking-222");
        payment.setUserId("user-333");
        payment.setAmount(1500.00);
        payment.setCurrency("INR");
        payment.setStatus("CREATED");

        when(paymentRepository.findAll()).thenReturn(List.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PaymentResponse response = paymentService.processWebhook("booking-222");

        // Assert
        assertNotNull(response);
        assertEquals("PAID", response.getStatus());
        assertEquals("payment-111", response.getId());
    }

    @Test
    @DisplayName("Should throw exception when payment not found for webhook")
    void testProcessWebhookNotFound() {
        // Arrange
        when(paymentRepository.findAll()).thenReturn(List.of());

        // Act & Assert
        AppException exception = assertThrows(AppException.class, 
            () -> paymentService.processWebhook("non-existent-booking"));
        assertEquals("Payment not found", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should find correct payment from multiple payments")
    void testProcessWebhookFindCorrectPayment() {
        // Arrange
        Payment payment1 = new Payment();
        payment1.setId("pay-1");
        payment1.setBookingId("booking-1");
        payment1.setStatus("CREATED");

        Payment payment2 = new Payment();
        payment2.setId("pay-2");
        payment2.setBookingId("booking-2");
        payment2.setStatus("CREATED");

        Payment payment3 = new Payment();
        payment3.setId("pay-3");
        payment3.setBookingId("booking-3");
        payment3.setStatus("CREATED");

        when(paymentRepository.findAll()).thenReturn(Arrays.asList(payment1, payment2, payment3));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PaymentResponse response = paymentService.processWebhook("booking-2");

        // Assert
        assertEquals("pay-2", response.getId());
        assertEquals("booking-2", response.getBookingId());
    }

    @Test
    @DisplayName("Should preserve booking id in payment response")
    void testCreatePaymentPreservesBookingId() {
        // Arrange
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setBookingId("bk-special-123");
        request.setUserId("u-xyz");
        request.setAmount(2000.00);

        Payment saved = new Payment();
        saved.setId("pay-special");
        saved.setBookingId("bk-special-123");
        saved.setStatus("CREATED");
        saved.setCurrency("INR");

        when(paymentRepository.save(any(Payment.class))).thenReturn(saved);

        // Act
        PaymentResponse response = paymentService.createPayment(request);

        // Assert
        assertEquals("bk-special-123", response.getBookingId());
    }

    @Test
    @DisplayName("Should preserve user id in payment response")
    void testCreatePaymentPreservesUserId() {
        // Arrange
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setBookingId("bk-abc");
        request.setUserId("user-special-999");
        request.setAmount(500.00);

        Payment saved = new Payment();
        saved.setId("pay-abc");
        saved.setUserId("user-special-999");
        saved.setStatus("CREATED");
        saved.setCurrency("INR");

        when(paymentRepository.save(any(Payment.class))).thenReturn(saved);

        // Act
        PaymentResponse response = paymentService.createPayment(request);

        // Assert
        assertEquals("user-special-999", response.getUserId());
    }

    @Test
    @DisplayName("Should preserve amount in payment response")
    void testCreatePaymentPreservesAmount() {
        // Arrange
        double amount = 2500.50;
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setBookingId("bk-amount");
        request.setUserId("u-amount");
        request.setAmount(amount);

        Payment saved = new Payment();
        saved.setId("pay-amount");
        saved.setAmount(amount);
        saved.setStatus("CREATED");
        saved.setCurrency("INR");

        when(paymentRepository.save(any(Payment.class))).thenReturn(saved);

        // Act
        PaymentResponse response = paymentService.createPayment(request);

        // Assert
        assertEquals(amount, response.getAmount());
    }
}

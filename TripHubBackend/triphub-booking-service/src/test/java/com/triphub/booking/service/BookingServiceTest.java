package com.triphub.booking.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.triphub.booking.domain.Registration;
import com.triphub.booking.dto.BookingRequest;
import com.triphub.booking.dto.BookingResponse;
import com.triphub.booking.repository.RegistrationRepository;
import com.triphub.shared.exception.AppException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookingService")
class BookingServiceTest {

    @Mock
    private RegistrationRepository registrationRepository;

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(registrationRepository);
    }

    @Test
    @DisplayName("Should create booking with all required fields")
    void testCreateBooking() {
        // Arrange
        BookingRequest request = new BookingRequest();
        request.setEventId("event-123");
        request.setUserId("user-456");

        Registration savedRegistration = new Registration();
        savedRegistration.setId("booking-789");
        savedRegistration.setEventId("event-123");
        savedRegistration.setUserId("user-456");
        savedRegistration.setStatus("PENDING_PAYMENT");
        savedRegistration.setReservationToken("token-abc123");

        when(registrationRepository.save(any(Registration.class))).thenReturn(savedRegistration);

        // Act
        BookingResponse response = bookingService.createBooking(request);

        // Assert
        assertNotNull(response);
        assertEquals("booking-789", response.getId());
        assertEquals("event-123", response.getEventId());
        assertEquals("user-456", response.getUserId());
        assertEquals("PENDING_PAYMENT", response.getStatus());
        assertEquals("token-abc123", response.getReservationToken());
        verify(registrationRepository, times(1)).save(any(Registration.class));
    }

    @Test
    @DisplayName("Should set initial status to PENDING_PAYMENT")
    void testCreateBookingInitialStatus() {
        // Arrange
        BookingRequest request = new BookingRequest();
        request.setEventId("evt-1");
        request.setUserId("usr-1");

        ArgumentCaptor<Registration> captor = ArgumentCaptor.forClass(Registration.class);

        Registration savedReg = new Registration();
        savedReg.setId("reg-1");
        savedReg.setStatus("PENDING_PAYMENT");

        when(registrationRepository.save(any(Registration.class))).thenReturn(savedReg);

        // Act
        bookingService.createBooking(request);

        // Assert
        verify(registrationRepository).save(captor.capture());
        assertEquals("PENDING_PAYMENT", captor.getValue().getStatus());
    }

    @Test
    @DisplayName("Should generate reservation token on booking creation")
    void testCreateBookingGeneratesToken() {
        // Arrange
        BookingRequest request = new BookingRequest();
        request.setEventId("evt-2");
        request.setUserId("usr-2");

        ArgumentCaptor<Registration> captor = ArgumentCaptor.forClass(Registration.class);

        Registration savedReg = new Registration();
        savedReg.setId("reg-2");
        savedReg.setReservationToken("generated-token");

        when(registrationRepository.save(any(Registration.class))).thenReturn(savedReg);

        // Act
        bookingService.createBooking(request);

        // Assert
        verify(registrationRepository).save(captor.capture());
        assertNotNull(captor.getValue().getReservationToken());
    }

    @Test
    @DisplayName("Should retrieve booking by id successfully")
    void testGetBookingById() {
        // Arrange
        Registration registration = new Registration();
        registration.setId("booking-101");
        registration.setEventId("event-202");
        registration.setUserId("user-303");
        registration.setStatus("PENDING_PAYMENT");
        registration.setReservationToken("token-xyz");

        when(registrationRepository.findById("booking-101")).thenReturn(Optional.of(registration));

        // Act
        BookingResponse response = bookingService.getBooking("booking-101");

        // Assert
        assertNotNull(response);
        assertEquals("booking-101", response.getId());
        assertEquals("event-202", response.getEventId());
        assertEquals("user-303", response.getUserId());
        assertEquals("PENDING_PAYMENT", response.getStatus());
        verify(registrationRepository, times(1)).findById("booking-101");
    }

    @Test
    @DisplayName("Should throw exception when booking not found")
    void testGetBookingNotFound() {
        // Arrange
        when(registrationRepository.findById("non-existent")).thenReturn(Optional.empty());

        // Act & Assert
        AppException exception = assertThrows(AppException.class, 
            () -> bookingService.getBooking("non-existent"));
        assertEquals("Booking not found", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should preserve event id in booking response")
    void testBookingPreservesEventId() {
        // Arrange
        BookingRequest request = new BookingRequest();
        request.setEventId("event-special-123");
        request.setUserId("user-xyz");

        Registration saved = new Registration();
        saved.setId("booking-special");
        saved.setEventId("event-special-123");

        when(registrationRepository.save(any(Registration.class))).thenReturn(saved);

        // Act
        BookingResponse response = bookingService.createBooking(request);

        // Assert
        assertEquals("event-special-123", response.getEventId());
    }

    @Test
    @DisplayName("Should preserve user id in booking response")
    void testBookingPreservesUserId() {
        // Arrange
        BookingRequest request = new BookingRequest();
        request.setEventId("evt-xyz");
        request.setUserId("user-special-789");

        Registration saved = new Registration();
        saved.setId("booking-xyz");
        saved.setUserId("user-special-789");

        when(registrationRepository.save(any(Registration.class))).thenReturn(saved);

        // Act
        BookingResponse response = bookingService.createBooking(request);

        // Assert
        assertEquals("user-special-789", response.getUserId());
    }
}

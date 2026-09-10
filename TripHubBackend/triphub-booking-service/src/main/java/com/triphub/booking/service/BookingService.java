package com.triphub.booking.service;

import com.triphub.booking.domain.Registration;
import com.triphub.booking.dto.BookingRequest;
import com.triphub.booking.dto.BookingResponse;
import com.triphub.booking.repository.RegistrationRepository;
import com.triphub.shared.exception.AppException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final RegistrationRepository registrationRepository;

    public BookingResponse createBooking(BookingRequest request) {
        Registration registration = new Registration();
        registration.setEventId(request.getEventId());
        registration.setUserId(request.getUserId());
        registration.setStatus("PENDING_PAYMENT");
        registration.setReservationToken(UUID.randomUUID().toString());

        Registration saved = registrationRepository.save(registration);
        return new BookingResponse(
                saved.getId(),
                saved.getEventId(),
                saved.getUserId(),
                saved.getStatus(),
                saved.getReservationToken()
        );
    }

    public BookingResponse getBooking(String bookingId) {
        Registration registration = registrationRepository.findById(bookingId)
                .orElseThrow(() -> new AppException("Booking not found", 404));

        return new BookingResponse(
                registration.getId(),
                registration.getEventId(),
                registration.getUserId(),
                registration.getStatus(),
                registration.getReservationToken()
        );
    }
}

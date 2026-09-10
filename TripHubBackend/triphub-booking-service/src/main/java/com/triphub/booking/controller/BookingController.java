package com.triphub.booking.controller;

import com.triphub.booking.dto.BookingRequest;
import com.triphub.booking.dto.BookingResponse;
import com.triphub.booking.service.BookingService;
import com.triphub.shared.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(@Valid @RequestBody BookingRequest request) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.createBooking(request), "Booking created successfully"));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBooking(@PathVariable String bookingId) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.getBooking(bookingId), "Booking retrieved successfully"));
    }
}

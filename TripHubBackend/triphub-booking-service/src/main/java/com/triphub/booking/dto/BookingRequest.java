package com.triphub.booking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookingRequest {

    @NotBlank(message = "Event ID is required")
    private String eventId;

    @NotBlank(message = "User ID is required")
    private String userId;
}

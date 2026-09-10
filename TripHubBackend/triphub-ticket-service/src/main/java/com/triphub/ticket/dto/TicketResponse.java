package com.triphub.ticket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {
    private String id;
    private String bookingId;
    private String userId;
    private String ticketCode;
    private String status;
}

package com.triphub.ticket.service;

import com.triphub.shared.exception.AppException;
import com.triphub.ticket.domain.Ticket;
import com.triphub.ticket.dto.CreateTicketRequest;
import com.triphub.ticket.dto.TicketResponse;
import com.triphub.ticket.repository.TicketRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketResponse createTicket(CreateTicketRequest request) {
        Ticket ticket = new Ticket();
        ticket.setBookingId(request.getBookingId());
        ticket.setUserId(request.getUserId());
        ticket.setTicketCode(UUID.randomUUID().toString());
        ticket.setStatus("ISSUED");

        Ticket saved = ticketRepository.save(ticket);
        return new TicketResponse(
                saved.getId(),
                saved.getBookingId(),
                saved.getUserId(),
                saved.getTicketCode(),
                saved.getStatus()
        );
    }

    public TicketResponse getTicket(String id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new AppException("Ticket not found", 404));

        return new TicketResponse(
                ticket.getId(),
                ticket.getBookingId(),
                ticket.getUserId(),
                ticket.getTicketCode(),
                ticket.getStatus()
        );
    }
}

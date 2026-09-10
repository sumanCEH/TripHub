package com.triphub.ticket.controller;

import com.triphub.shared.common.ApiResponse;
import com.triphub.ticket.dto.CreateTicketRequest;
import com.triphub.ticket.dto.TicketResponse;
import com.triphub.ticket.service.TicketService;
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
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<ApiResponse<TicketResponse>> createTicket(@Valid @RequestBody CreateTicketRequest request) {
        return ResponseEntity.ok(ApiResponse.success(ticketService.createTicket(request), "Ticket created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketResponse>> getTicket(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(ticketService.getTicket(id), "Ticket retrieved successfully"));
    }
}

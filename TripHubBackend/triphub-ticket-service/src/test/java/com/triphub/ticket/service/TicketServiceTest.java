package com.triphub.ticket.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.triphub.shared.exception.AppException;
import com.triphub.ticket.domain.Ticket;
import com.triphub.ticket.dto.CreateTicketRequest;
import com.triphub.ticket.dto.TicketResponse;
import com.triphub.ticket.repository.TicketRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("TicketService")
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        ticketService = new TicketService(ticketRepository);
    }

    @Test
    @DisplayName("Should create ticket with all required fields")
    void testCreateTicket() {
        // Arrange
        CreateTicketRequest request = new CreateTicketRequest();
        request.setBookingId("booking-123");
        request.setUserId("user-456");

        Ticket savedTicket = new Ticket();
        savedTicket.setId("ticket-789");
        savedTicket.setBookingId("booking-123");
        savedTicket.setUserId("user-456");
        savedTicket.setTicketCode("code-abc123xyz");
        savedTicket.setStatus("ISSUED");

        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);

        // Act
        TicketResponse response = ticketService.createTicket(request);

        // Assert
        assertNotNull(response);
        assertEquals("ticket-789", response.getId());
        assertEquals("booking-123", response.getBookingId());
        assertEquals("user-456", response.getUserId());
        assertEquals("code-abc123xyz", response.getTicketCode());
        assertEquals("ISSUED", response.getStatus());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Should generate unique ticket code on creation")
    void testCreateTicketGeneratesUniqueCode() {
        // Arrange
        CreateTicketRequest request = new CreateTicketRequest();
        request.setBookingId("bk-1");
        request.setUserId("usr-1");

        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);

        Ticket savedTicket = new Ticket();
        savedTicket.setId("tkt-1");
        savedTicket.setTicketCode("unique-code-uuid");
        savedTicket.setStatus("ISSUED");

        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);

        // Act
        ticketService.createTicket(request);

        // Assert
        verify(ticketRepository).save(captor.capture());
        assertNotNull(captor.getValue().getTicketCode());
    }

    @Test
    @DisplayName("Should set initial status to ISSUED")
    void testCreateTicketInitialStatus() {
        // Arrange
        CreateTicketRequest request = new CreateTicketRequest();
        request.setBookingId("bk-2");
        request.setUserId("usr-2");

        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);

        Ticket savedTicket = new Ticket();
        savedTicket.setStatus("ISSUED");

        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);

        // Act
        ticketService.createTicket(request);

        // Assert
        verify(ticketRepository).save(captor.capture());
        assertEquals("ISSUED", captor.getValue().getStatus());
    }

    @Test
    @DisplayName("Should retrieve ticket by id successfully")
    void testGetTicketById() {
        // Arrange
        Ticket ticket = new Ticket();
        ticket.setId("ticket-100");
        ticket.setBookingId("booking-200");
        ticket.setUserId("user-300");
        ticket.setTicketCode("TKT-12345-ABCDE");
        ticket.setStatus("ISSUED");

        when(ticketRepository.findById("ticket-100")).thenReturn(Optional.of(ticket));

        // Act
        TicketResponse response = ticketService.getTicket("ticket-100");

        // Assert
        assertNotNull(response);
        assertEquals("ticket-100", response.getId());
        assertEquals("booking-200", response.getBookingId());
        assertEquals("user-300", response.getUserId());
        assertEquals("TKT-12345-ABCDE", response.getTicketCode());
        assertEquals("ISSUED", response.getStatus());
        verify(ticketRepository, times(1)).findById("ticket-100");
    }

    @Test
    @DisplayName("Should throw exception when ticket not found")
    void testGetTicketNotFound() {
        // Arrange
        when(ticketRepository.findById("non-existent")).thenReturn(Optional.empty());

        // Act & Assert
        AppException exception = assertThrows(AppException.class, 
            () -> ticketService.getTicket("non-existent"));
        assertEquals("Ticket not found", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should preserve booking id in ticket response")
    void testCreateTicketPreservesBookingId() {
        // Arrange
        CreateTicketRequest request = new CreateTicketRequest();
        request.setBookingId("bk-special-123");
        request.setUserId("usr-xyz");

        Ticket saved = new Ticket();
        saved.setId("tkt-special");
        saved.setBookingId("bk-special-123");
        saved.setTicketCode("code");
        saved.setStatus("ISSUED");

        when(ticketRepository.save(any(Ticket.class))).thenReturn(saved);

        // Act
        TicketResponse response = ticketService.createTicket(request);

        // Assert
        assertEquals("bk-special-123", response.getBookingId());
    }

    @Test
    @DisplayName("Should preserve user id in ticket response")
    void testCreateTicketPreservesUserId() {
        // Arrange
        CreateTicketRequest request = new CreateTicketRequest();
        request.setBookingId("bk-abc");
        request.setUserId("user-special-999");

        Ticket saved = new Ticket();
        saved.setId("tkt-abc");
        saved.setUserId("user-special-999");
        saved.setTicketCode("code");
        saved.setStatus("ISSUED");

        when(ticketRepository.save(any(Ticket.class))).thenReturn(saved);

        // Act
        TicketResponse response = ticketService.createTicket(request);

        // Assert
        assertEquals("user-special-999", response.getUserId());
    }

    @Test
    @DisplayName("Should map all ticket fields to response correctly")
    void testTicketResponseMapping() {
        // Arrange
        Ticket ticket = new Ticket();
        ticket.setId("tkt-500");
        ticket.setBookingId("bk-600");
        ticket.setUserId("usr-700");
        ticket.setTicketCode("TICKET-CODE-999");
        ticket.setStatus("ISSUED");

        when(ticketRepository.findById("tkt-500")).thenReturn(Optional.of(ticket));

        // Act
        TicketResponse response = ticketService.getTicket("tkt-500");

        // Assert
        assertEquals("tkt-500", response.getId());
        assertEquals("bk-600", response.getBookingId());
        assertEquals("usr-700", response.getUserId());
        assertEquals("TICKET-CODE-999", response.getTicketCode());
        assertEquals("ISSUED", response.getStatus());
    }
}

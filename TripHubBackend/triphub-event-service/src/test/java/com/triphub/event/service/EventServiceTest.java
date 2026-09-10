package com.triphub.event.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.triphub.event.domain.EventEntity;
import com.triphub.event.dto.CreateEventRequest;
import com.triphub.event.dto.EventResponse;
import com.triphub.event.repository.EventRepository;
import com.triphub.shared.exception.AppException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventService")
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventService = new EventService(eventRepository);
    }

    @Test
    @DisplayName("Should return all events from repository")
    void testGetAllEvents() {
        // Arrange
        EventEntity event1 = new EventEntity();
        event1.setId("1");
        event1.setTitle("Conference 2026");
        event1.setCapacity(500);
        event1.setSeatsAvailable(500);

        EventEntity event2 = new EventEntity();
        event2.setId("2");
        event2.setTitle("Workshop");
        event2.setCapacity(50);
        event2.setSeatsAvailable(45);

        when(eventRepository.findAll()).thenReturn(Arrays.asList(event1, event2));

        // Act
        List<EventResponse> events = eventService.getAllEvents();

        // Assert
        assertNotNull(events);
        assertEquals(2, events.size());
        assertEquals("Conference 2026", events.get(0).getTitle());
        assertEquals("Workshop", events.get(1).getTitle());
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no events exist")
    void testGetAllEventsWhenEmpty() {
        // Arrange
        when(eventRepository.findAll()).thenReturn(List.of());

        // Act
        List<EventResponse> events = eventService.getAllEvents();

        // Assert
        assertNotNull(events);
        assertTrue(events.isEmpty());
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve event by id successfully")
    void testGetEventById() {
        // Arrange
        EventEntity event = new EventEntity();
        event.setId("event-123");
        event.setTitle("Tech Summit");
        event.setDescription("Annual tech summit");
        event.setVenue("Convention Center");
        event.setCapacity(1000);
        event.setSeatsAvailable(750);
        event.setOrganizerId("org-1");

        when(eventRepository.findById("event-123")).thenReturn(Optional.of(event));

        // Act
        EventResponse response = eventService.getEventById("event-123");

        // Assert
        assertNotNull(response);
        assertEquals("event-123", response.getId());
        assertEquals("Tech Summit", response.getTitle());
        assertEquals("Annual tech summit", response.getDescription());
        assertEquals("Convention Center", response.getVenue());
        verify(eventRepository, times(1)).findById("event-123");
    }

    @Test
    @DisplayName("Should throw exception when event not found")
    void testGetEventByIdNotFound() {
        // Arrange
        when(eventRepository.findById("non-existent")).thenReturn(Optional.empty());

        // Act & Assert
        AppException exception = assertThrows(AppException.class, 
            () -> eventService.getEventById("non-existent"));
        assertEquals("Event not found", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
        verify(eventRepository, times(1)).findById("non-existent");
    }

    @Test
    @DisplayName("Should create event with all fields set correctly")
    void testCreateEvent() {
        // Arrange
        CreateEventRequest request = new CreateEventRequest();
        request.setTitle("Product Launch");
        request.setDescription("New product unveiling");
        request.setVenue("Main Hall");
        request.setCapacity(300);
        LocalDateTime now = LocalDateTime.now();
        request.setStartDate(now.toString());
        request.setEndDate(now.plusHours(4).toString());

        EventEntity savedEvent = new EventEntity();
        savedEvent.setId("event-456");
        savedEvent.setTitle(request.getTitle());
        savedEvent.setDescription(request.getDescription());
        savedEvent.setVenue(request.getVenue());
        savedEvent.setCapacity(request.getCapacity());
        savedEvent.setStartDate(now.toString());
        savedEvent.setEndDate(now.plusHours(4).toString());
        savedEvent.setSeatsAvailable(request.getCapacity());
        savedEvent.setOrganizerId("org-123");

        when(eventRepository.save(any(EventEntity.class))).thenReturn(savedEvent);

        // Act
        EventResponse response = eventService.createEvent(request, "org-123");

        // Assert
        assertNotNull(response);
        assertEquals("Product Launch", response.getTitle());
        assertEquals(300, response.getCapacity());
        assertEquals(300, response.getSeatsAvailable());
        assertEquals("org-123", response.getOrganizerId());
        verify(eventRepository, times(1)).save(any(EventEntity.class));
    }

    @Test
    @DisplayName("Should initialize seats available equal to capacity")
    void testCreateEventSeatsInitialization() {
        // Arrange
        CreateEventRequest request = new CreateEventRequest();
        request.setTitle("Concert");
        request.setCapacity(5000);

        EventEntity savedEvent = new EventEntity();
        savedEvent.setId("event-concert");
        savedEvent.setTitle("Concert");
        savedEvent.setCapacity(5000);
        savedEvent.setSeatsAvailable(5000);

        when(eventRepository.save(any(EventEntity.class))).thenReturn(savedEvent);

        // Act
        EventResponse response = eventService.createEvent(request, "org-music");

        // Assert
        assertEquals(5000, response.getCapacity());
        assertEquals(5000, response.getSeatsAvailable());
    }

    @Test
    @DisplayName("Should set organizer id for created event")
    void testCreateEventWithOrganizerId() {
        // Arrange
        CreateEventRequest request = new CreateEventRequest();
        request.setTitle("Webinar");
        request.setCapacity(200);

        EventEntity savedEvent = new EventEntity();
        savedEvent.setId("webinar-123");
        savedEvent.setTitle("Webinar");
        savedEvent.setCapacity(200);
        savedEvent.setSeatsAvailable(200);
        savedEvent.setOrganizerId("organizer-789");

        when(eventRepository.save(any(EventEntity.class))).thenReturn(savedEvent);

        // Act
        EventResponse response = eventService.createEvent(request, "organizer-789");

        // Assert
        assertEquals("organizer-789", response.getOrganizerId());
    }

    @Test
    @DisplayName("Should map all event fields to response correctly")
    void testEventResponseMapping() {
        // Arrange
        EventEntity event = new EventEntity();
        event.setId("evt-100");
        event.setTitle("Festival");
        event.setDescription("Music festival");
        event.setVenue("Outdoor Amphitheater");
        LocalDateTime start = LocalDateTime.of(2026, 7, 15, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 15, 22, 0);
        event.setStartDate(start.toString());
        event.setEndDate(end.toString());
        event.setCapacity(10000);
        event.setSeatsAvailable(8500);
        event.setOrganizerId("festivals-inc");

        // Act
        EventResponse response = new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getVenue(),
                event.getStartDate(),
                event.getEndDate(),
                event.getCapacity(),
                event.getSeatsAvailable(),
                event.getOrganizerId()
        );

        // Assert
        assertEquals("evt-100", response.getId());
        assertEquals("Festival", response.getTitle());
        assertEquals("Music festival", response.getDescription());
        assertEquals("Outdoor Amphitheater", response.getVenue());
        assertEquals(start.toString(), response.getStartDate());
        assertEquals(end.toString(), response.getEndDate());
        assertEquals(10000, response.getCapacity());
        assertEquals(8500, response.getSeatsAvailable());
        assertEquals("festivals-inc", response.getOrganizerId());
    }

    @Test
    @DisplayName("Should handle multiple events with different capacities")
    void testGetAllEventsWithVariousCapacities() {
        // Arrange
        EventEntity small = new EventEntity();
        small.setId("1");
        small.setTitle("Meetup");
        small.setCapacity(30);

        EventEntity medium = new EventEntity();
        medium.setId("2");
        medium.setTitle("Seminar");
        medium.setCapacity(300);

        EventEntity large = new EventEntity();
        large.setId("3");
        large.setTitle("Convention");
        large.setCapacity(3000);

        when(eventRepository.findAll()).thenReturn(Arrays.asList(small, medium, large));

        // Act
        List<EventResponse> events = eventService.getAllEvents();

        // Assert
        assertEquals(3, events.size());
        assertEquals(30, events.get(0).getCapacity());
        assertEquals(300, events.get(1).getCapacity());
        assertEquals(3000, events.get(2).getCapacity());
    }
}

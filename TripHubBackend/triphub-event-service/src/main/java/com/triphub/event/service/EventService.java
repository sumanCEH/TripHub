package com.triphub.event.service;

import com.triphub.event.domain.EventEntity;
import com.triphub.event.dto.CreateEventRequest;
import com.triphub.event.dto.EventResponse;
import com.triphub.event.repository.EventRepository;
import com.triphub.shared.exception.AppException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public EventResponse getEventById(String id) {
        EventEntity event = eventRepository.findById(id)
                .orElseThrow(() -> new AppException("Event not found", 404));
        return toResponse(event);
    }

    public EventResponse createEvent(CreateEventRequest request, String organizerId) {
        EventEntity event = new EventEntity();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setVenue(request.getVenue());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setCapacity(request.getCapacity());
        event.setSeatsAvailable(request.getCapacity());
        event.setOrganizerId(organizerId);

        EventEntity saved = eventRepository.save(event);
        return toResponse(saved);
    }

    private EventResponse toResponse(EventEntity event) {
        return new EventResponse(
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
    }
}

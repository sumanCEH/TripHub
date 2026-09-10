package com.triphub.event.controller;

import com.triphub.event.dto.CreateEventRequest;
import com.triphub.event.dto.EventResponse;
import com.triphub.event.service.EventService;
import com.triphub.shared.common.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EventResponse>>> getAllEvents() {
        return ResponseEntity.ok(ApiResponse.success(eventService.getAllEvents(), "Events fetched successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> getEventById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(eventService.getEventById(id), "Event fetched successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(@Valid @RequestBody CreateEventRequest request) {
        String organizerId = "org-001";
        return ResponseEntity.ok(ApiResponse.success(eventService.createEvent(request, organizerId), "Event created successfully"));
    }
}

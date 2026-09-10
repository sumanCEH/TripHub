package com.triphub.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    private String id;
    private String title;
    private String description;
    private String venue;
    private String startDate;
    private String endDate;
    private Integer capacity;
    private Integer seatsAvailable;
    private String organizerId;
}

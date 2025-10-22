package com.seatify.controller;

import com.seatify.dto.admin.response.EventResponseDTO;
import com.seatify.dto.response.PublicEventResponseDTO;
import com.seatify.service.admin.AdminEventService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final AdminEventService eventService;

    @Operation(summary = "Lấy danh sách tất cả sự kiện")
    @GetMapping
    public ResponseEntity<Page<PublicEventResponseDTO>> getAllEvents(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startTime") String sortBy,
            @RequestParam(defaultValue = "false") boolean desc
    ) {
        Page<EventResponseDTO> events = eventService.getAll(name, page, size, sortBy, desc);
        Page<PublicEventResponseDTO> publicEvents = events.map(this::convertToPublicDTO);
        return ResponseEntity.ok(publicEvents);
    }

    @Operation(summary = "Xem chi tiết sự kiện")
    @GetMapping("/{id}")
    public ResponseEntity<PublicEventResponseDTO> getEventById(@PathVariable Long id) {
        EventResponseDTO event = eventService.getById(id);
        return ResponseEntity.ok(convertToPublicDTO(event));
    }

    private PublicEventResponseDTO convertToPublicDTO(EventResponseDTO event) {
        return PublicEventResponseDTO.builder()
                .eventId(event.getEventId())
                .eventName(event.getEventName())
                .description(event.getDescription())
                .location(event.getLocation())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .capacity(event.getCapacity())
                .status(event.getStatus())
                .thumbnail(event.getThumbnail())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}

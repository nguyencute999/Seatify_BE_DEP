package com.seatify.dto.admin.response;

import com.seatify.model.constants.EventStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventResponseDTO {
    private Long eventId;
    private String eventName;
    private String description;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer capacity;
    private EventStatus status;
    private String thumbnail;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

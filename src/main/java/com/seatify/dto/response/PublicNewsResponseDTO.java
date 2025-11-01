package com.seatify.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PublicNewsResponseDTO {
    private Long newsId;
    private Long eventId;
    private String eventName;
    private String title;
    private String content;
    private String thumbnail;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
}

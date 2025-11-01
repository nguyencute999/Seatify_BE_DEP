package com.seatify.dto.admin.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminNewsResponseDTO {
    private Long newsId;
    private Long eventId;
    private String eventName; // Tên sự kiện
    private String title;
    private String content;
    private String thumbnail;
    private String authorEmail;
    private LocalDateTime publishedAt; // Thời gian công bố
    private LocalDateTime createdAt; // Thời gian tạo tin tức
    private Boolean isPublished; // Trạng thái publish/unpublish
}



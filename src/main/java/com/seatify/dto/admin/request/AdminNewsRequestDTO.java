package com.seatify.dto.admin.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO cho CRUD tin tức (admin)
 * Ví dụ:
 * {
 *   "title": "Khai mạc sự kiện công nghệ 2025",
 *   "content": "Sự kiện sẽ diễn ra vào tuần tới với nhiều hoạt động...",
 *   "thumbnail": "https://example.com/news-thumb.jpg",
 *   "eventId": 1
 * }
 */
@Data
public class AdminNewsRequestDTO {
    @NotBlank
    @Size(max = 200)
    private String title;

    @NotBlank
    private String content;

    private String thumbnail;

    // Có thể gắn tin tức vào 1 sự kiện cụ thể (tuỳ chọn)
    private Long eventId;
}



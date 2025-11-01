package com.seatify.dto.user.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO request để tạo và cập nhật bình luận sự kiện
 * - eventId: bắt buộc khi tạo, tùy chọn khi cập nhật
 */
@Data
public class EventCommentRequest {
    
    private Long eventId; // Bắt buộc khi tạo, tùy chọn khi cập nhật
    
    @NotBlank(message = "Comment content is required")
    @Size(min = 1, max = 1000, message = "Comment must be between 1 and 1000 characters")
    private String content;
    
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1 star")
    @Max(value = 5, message = "Rating must be at most 5 stars")
    private Integer rating; // Đánh giá từ 1-5 sao
}

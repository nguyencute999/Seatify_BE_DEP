package com.seatify.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO response cho bình luận sự kiện
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCommentResponse {
    private Long commentId;
    private Long eventId;
    private String eventName;
    private Long userId;
    private String userName;
    private String userEmail;
    private String userAvatar; // Avatar URL của người bình luận
    private String content;
    private Integer rating; // Đánh giá từ 1-5 sao
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

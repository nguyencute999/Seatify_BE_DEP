package com.seatify.dto.admin.response;

import com.seatify.model.constants.EventStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO cho việc phản hồi thông tin sự kiện từ hệ thống quản trị.
 * Dùng để gửi dữ liệu sự kiện từ backend về cho client (ví dụ: trong trang quản lý sự kiện).
 *
 * Chứa các thông tin chi tiết của sự kiện bao gồm:
 * - Thông tin cơ bản (tên, mô tả, địa điểm)
 * - Thời gian bắt đầu và kết thúc
 * - Trạng thái sự kiện (EventStatus)
 * - Thông tin người tạo và thời gian tạo/cập nhật
 *
 * @author : Lê Văn Nguyễn - CE181235
 */
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

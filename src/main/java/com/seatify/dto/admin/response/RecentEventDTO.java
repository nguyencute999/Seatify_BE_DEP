package com.seatify.dto.admin.response;

import com.seatify.model.constants.EventStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO cho thông tin sự kiện gần đây trong dashboard.
 * 
 * @author : Lê Văn Nguyễn - CE181235
 */
@Data
@Builder
public class RecentEventDTO {
    private Long id;
    private String name;
    private LocalDateTime startTime;
    private Integer capacity;
    private Integer booked; // Số chỗ đã được đặt
    private EventStatus status;
}


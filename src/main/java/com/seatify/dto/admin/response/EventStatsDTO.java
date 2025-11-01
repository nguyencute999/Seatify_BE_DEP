package com.seatify.dto.admin.response;

import com.seatify.model.constants.EventStatus;
import lombok.Builder;
import lombok.Data;

/**
 * DTO cho thống kê sự kiện.
 * 
 * @author : Lê Văn Nguyễn - CE181235
 */
@Data
@Builder
public class EventStatsDTO {
    private Long id;
    private String eventName;
    private Long totalBookings;
    private Long attendance; // Số lượng đã check-in
    private Long revenue;
    private EventStatus status;
    private Double attendanceRate; // Tỷ lệ tham gia (%)
}


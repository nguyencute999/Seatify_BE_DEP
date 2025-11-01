package com.seatify.dto.admin.response;

import com.seatify.model.constants.BookingStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO cho thông tin đặt chỗ gần đây trong dashboard.
 * 
 * @author : Lê Văn Nguyễn - CE181235
 */
@Data
@Builder
public class RecentBookingDTO {
    private Long id;
    private String user; // Tên người dùng
    private String event; // Tên sự kiện
    private LocalDateTime bookingTime;
    private BookingStatus status;
}


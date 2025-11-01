package com.seatify.dto.admin.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO cho xu hướng đặt chỗ theo ngày.
 * 
 * @author : Lê Văn Nguyễn - CE181235
 */
@Data
@Builder
public class BookingTrendDTO {
    private LocalDate date;
    private Long bookings;
}


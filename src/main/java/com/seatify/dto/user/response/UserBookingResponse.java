package com.seatify.dto.user.response;

import com.seatify.model.constants.BookingStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserBookingResponse {
    private Long bookingId;
    private Long eventId;
    private Long seatId;
    private String qrCode;
    private BookingStatus status;
    private LocalDateTime bookingTime;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
}



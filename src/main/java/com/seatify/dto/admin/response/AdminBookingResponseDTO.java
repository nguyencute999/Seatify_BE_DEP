package com.seatify.dto.admin.response;

import com.seatify.model.constants.BookingStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminBookingResponseDTO {
    private Long bookingId;
    private UserInfo user;
    private EventInfo event;
    private SeatInfo seat;
    private BookingStatus status;
    private LocalDateTime bookingTime;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    @Data
    @Builder
    public static class UserInfo {
        private Long userId;
        private String fullName;
        private String email;
        private String mssv;
        private String phone;
    }
    @Data
    @Builder
    public static class EventInfo {
        private Long eventId;
        private String eventName;
        private String location;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
    }
    @Data
    @Builder
    public static class SeatInfo {
        private Long seatId;
        private String seatRow;
        private Integer seatNumber;
    }
}

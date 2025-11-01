package com.seatify.dto.user.response;

import com.seatify.model.constants.AttendanceAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO response cho check-in/checkout
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckInResponse {
    private Long bookingId;
    private Long eventId;
    private String eventName;
    private String seatLabel;
    private AttendanceAction action; // CHECK_IN hoặc CHECK_OUT
    private LocalDateTime timestamp;
    private String message;
    private boolean autoCheckedOut; // true nếu tự động checkout vì thời gian check-in < 5s
}

package com.seatify.dto.user.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserBookingRequest {
    @NotNull
    private Long eventId;

    @NotNull
    private Long seatId;
}



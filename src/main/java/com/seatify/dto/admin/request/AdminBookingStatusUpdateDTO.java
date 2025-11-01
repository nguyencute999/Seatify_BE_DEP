package com.seatify.dto.admin.request;

import com.seatify.model.constants.BookingStatus;
import lombok.Data;

@Data
public class AdminBookingStatusUpdateDTO {
    private BookingStatus status;
}

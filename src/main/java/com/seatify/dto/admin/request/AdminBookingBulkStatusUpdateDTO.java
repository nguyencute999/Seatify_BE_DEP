package com.seatify.dto.admin.request;

import com.seatify.model.constants.BookingStatus;
import lombok.Data;

import java.util.List;

@Data
public class AdminBookingBulkStatusUpdateDTO {
    private List<Long> bookingIds;
    private BookingStatus status;
}

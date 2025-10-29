package com.seatify.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeatResponseDTO {
    private Long seatId;
    private String seatRow;
    private Integer seatNumber;
    private Boolean isAvailable;
    private Long eventId;
}

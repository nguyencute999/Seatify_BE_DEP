package com.seatify.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO cho request check-in bằng QR code
 * QR code data format: SEATIFY:seatId:userId:eventId:UUID
 */
@Data
public class CheckInRequest {
    @NotBlank(message = "QR code data is required")
    private String qrCodeData;
}

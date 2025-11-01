package com.seatify.controller;

import com.seatify.dto.user.request.CheckInRequest;
import com.seatify.dto.user.response.CheckInResponse;
import com.seatify.service.user.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller xử lý check-in và checkout bằng QR code
 * 
 * Author: Lê Văn Nguyễn - CE181235
 */
@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance", description = "APIs for event check-in and checkout using QR code")
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * API check-in từ QR code
     * 
     * Logic:
     * - Nếu chưa check-in: Thực hiện check-in
     * - Nếu đã check-in và chưa checkout: Tự động checkout (toggle)
     *   + Nếu thời gian check-in < 5s: Đánh dấu autoCheckedOut = true (coi như check-in nhầm)
     * - Nếu đã checkout: Cho phép check-in lại (toggle)
     * 
     * QR code format: SEATIFY:seatId:userId:eventId:UUID
     * 
     * @param request QR code data từ client
     * @return Kết quả check-in
     */
    @Operation(
        summary = "Check-in/Checkout bằng QR code (Toggle)",
        description = "Tự động toggle giữa check-in và checkout khi scan QR code. " +
                     "Lần scan đầu: check-in, lần scan thứ 2: checkout, lần scan thứ 3: check-in lại... " +
                     "Nếu thời gian check-in < 5 giây trước khi checkout, sẽ đánh dấu autoCheckedOut = true. " +
                     "QR code format: SEATIFY:seatId:userId:eventId:UUID"
    )
    @PostMapping("/check-in")
    public ResponseEntity<CheckInResponse> checkIn(@Valid @RequestBody CheckInRequest request) {
        CheckInResponse response = attendanceService.processCheckIn(request);
        return ResponseEntity.ok(response);
    }

    /**
     * API checkout từ QR code
     * 
     * Logic:
     * - Phải đã check-in mới có thể checkout
     * - Nếu thời gian check-in < 5s: Tự động checkout (coi như check-in nhầm)
     * - Nếu thời gian check-in >= 5s: Checkout bình thường
     * 
     * QR code format: SEATIFY:seatId:userId:eventId:UUID
     * 
     * @param request QR code data từ client
     * @return Kết quả checkout
     */
    @Operation(
        summary = "Checkout bằng QR code",
        description = "Thực hiện checkout từ QR code. " +
                     "Phải đã check-in mới có thể checkout. " +
                     "Nếu thời gian check-in < 5 giây, sẽ tự động checkout (coi như check-in nhầm). " +
                     "QR code format: SEATIFY:seatId:userId:eventId:UUID"
    )
    @PostMapping("/checkout")
    public ResponseEntity<CheckInResponse> checkout(@Valid @RequestBody CheckInRequest request) {
        CheckInResponse response = attendanceService.processCheckout(request);
        return ResponseEntity.ok(response);
    }
}

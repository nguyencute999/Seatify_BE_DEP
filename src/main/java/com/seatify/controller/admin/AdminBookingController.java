package com.seatify.controller.admin;

import com.seatify.dto.admin.request.AdminBookingBulkStatusUpdateDTO;
import com.seatify.dto.admin.request.AdminBookingStatusUpdateDTO;
import com.seatify.dto.admin.response.AdminBookingResponseDTO;
import com.seatify.service.admin.AdminBookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * @author : Lê Văn Nguyễn - CE181235
 *
 * Controller admin để quản lý các đặt chỗ (bookings).
 * Bao gồm các chức năng:
 * - Lấy danh sách đặt chỗ (lọc, tìm kiếm, phân trang)
 * - Xem chi tiết một đặt chỗ
 * - Cập nhật trạng thái đặt chỗ
 */
@RestController
@RequestMapping("/api/v1/admin/bookings")
@RequiredArgsConstructor
public class AdminBookingController {
    private final AdminBookingService adminBookingService;

    /**
     * Lấy thông tin chi tiết của một đặt chỗ cụ thể.
     *
     * @param bookingId ID của đặt chỗ
     * @return Thông tin chi tiết booking
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<AdminBookingResponseDTO> getBookingDetail(@PathVariable Long bookingId) {
        AdminBookingResponseDTO result = adminBookingService.getBookingDetail(bookingId);
        return ResponseEntity.ok(result);
    }

    /**
     * Cập nhật trạng thái của một đặt chỗ
     *
     * @param bookingId ID của đặt chỗ cần cập nhật
     * @param dto       Dữ liệu cập nhật trạng thái
     * @return AdminBookingResponseDTO sau khi cập nhật
     */
    @PutMapping("/{bookingId}/status")
    public ResponseEntity<AdminBookingResponseDTO> updateBookingStatus(
            @PathVariable Long bookingId,
            @Valid @RequestBody AdminBookingStatusUpdateDTO dto
    ) {
        AdminBookingResponseDTO result = adminBookingService.updateBookingStatus(bookingId, dto);
        return ResponseEntity.ok(result);
    }

    /**
     * Lấy danh sách đặt chỗ cho admin, có thể tìm kiếm, lọc theo trạng thái và sự kiện.
     *
     * @param search   Từ khóa tìm kiếm (mã booking, tên người dùng, email, v.v.)
     * @param status   Trạng thái booking (ALL, PENDING, CONFIRMED, CANCELLED, ...)
     * @param eventId  ID của sự kiện (tùy chọn)
     * @param page     Số trang (mặc định 0)
     * @param size     Kích thước trang (mặc định 20)
     * @param sortBy   Trường để sắp xếp (mặc định "bookingId")
     * @param desc     Sắp xếp giảm dần nếu true
     * @return Page<AdminBookingResponseDTO> danh sách đặt chỗ
     */
    @GetMapping
    public ResponseEntity<Page<AdminBookingResponseDTO>> getBookings(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(required = false) Long eventId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "bookingId") String sortBy,
            @RequestParam(defaultValue = "false") boolean desc
    ) {
        Page<AdminBookingResponseDTO> bookings = adminBookingService.getBookings(search, status, eventId, page, size, sortBy, desc);
        return ResponseEntity.ok(bookings);
    }

//    @PutMapping("/bulk-status")
//    public ResponseEntity<List<AdminBookingResponseDTO>> bulkUpdateStatus(
//            @Valid @RequestBody AdminBookingBulkStatusUpdateDTO dto
//    ) {
//        List<AdminBookingResponseDTO> result = adminBookingService.bulkUpdateStatus(dto);
//        return ResponseEntity.ok(result);
//    }

//    @DeleteMapping("/{bookingId}")
//    public ResponseEntity<Void> deleteBooking(@PathVariable Long bookingId) {
//        adminBookingService.deleteBooking(bookingId);
//        return ResponseEntity.noContent().build();
//    }
}

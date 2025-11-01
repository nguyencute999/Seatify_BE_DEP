package com.seatify.controller.user;

import com.seatify.dto.user.request.UserBookingRequest;
import com.seatify.dto.user.response.UserBookingResponse;
import com.seatify.dto.user.response.UserAttendanceStatsResponse;
import com.seatify.service.user.BookingService;
import com.seatify.service.user.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Author: Lê Văn Nguyễn - CE181235
 * Description: Controller dành cho người dùng (User) để quản lý đặt chỗ (Booking)
 * Bao gồm các chức năng: đặt chỗ, xem danh sách đặt chỗ, xem chi tiết, và hủy đặt chỗ.
 */
@RestController
@RequestMapping("/api/v1/user/bookings")
@RequiredArgsConstructor
@Tag(name = "User Booking", description = "APIs for user booking management")
@SecurityRequirement(name = "bearerAuth")//Yêu cầu token JWT để auth
public class BookingController {

    private final BookingService bookingService;//Xử lý logic đặt chỗ
    private final AuthService authService;//Lấy thông tin người dùng

    /**
     * Đặt chỗ cho sự kiện
     *
     * @param request        Dữ liệu đặt chỗ (eventId, số lượng vé, loại vé, v.v.)
     * @param authentication Thông tin xác thực từ JWT (chứa email user)
     * @return Thông tin đặt chỗ vừa được tạo (UserBookingResponse)
     */
    @Operation(summary = "Đặt chỗ cho sự kiện")
    @PostMapping
    public ResponseEntity<UserBookingResponse> createBooking(
            @Valid @RequestBody UserBookingRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        UserBookingResponse response = bookingService.createBooking(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lấy danh sách tất cả đặt chỗ của user hiện tại
     *
     * @param authentication Thông tin xác thực JWT để lấy userId
     * @return Danh sách các đặt chỗ của người dùng hiện tại
     */

  
    /**
     * Lấy lịch sử đặt chỗ của user hiện tại (alias của danh sách đặt chỗ)
     * Trả về danh sách kèm đường dẫn QR code cho từng đặt chỗ
     */
    @Operation(summary = "Lấy lịch sử đặt chỗ của user (có QR code)")
    @GetMapping("/history")
    public ResponseEntity<List<UserBookingResponse>> getUserBookingHistory(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<UserBookingResponse> bookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    @Operation(summary = "Thống kê sự kiện tham gia (tổng, có mặt, vắng mặt)")
    @GetMapping("/stats")
    public ResponseEntity<UserAttendanceStatsResponse> getUserAttendanceStats(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        UserAttendanceStatsResponse stats = bookingService.getUserAttendanceStats(userId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Lấy chi tiết một đặt chỗ cụ thể theo ID
     *
     * @param bookingId      ID của đặt chỗ cần xem chi tiết
     * @param authentication Xác thực người dùng hiện tại (JWT)
     * @return Thông tin chi tiết đặt chỗ (UserBookingResponse)
     */
    @Operation(summary = "Lấy chi tiết đặt chỗ theo ID")
    @GetMapping("/{bookingId}")
    public ResponseEntity<UserBookingResponse> getBookingById(
            @PathVariable Long bookingId,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        UserBookingResponse booking = bookingService.getBookingById(bookingId, userId);
        return ResponseEntity.ok(booking);
    }

    /**
     * Hủy một đặt chỗ
     *
     * @param bookingId      ID của đặt chỗ cần hủy
     * @param authentication Xác thực người dùng hiện tại
     * @return Thông tin đặt chỗ sau khi hủy (UserBookingResponse)
     */
    @Operation(summary = "Hủy đặt chỗ")
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<UserBookingResponse> cancelBooking(
            @PathVariable Long bookingId,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        UserBookingResponse booking = bookingService.cancelBooking(bookingId, userId);
        return ResponseEntity.ok(booking);
    }

    /**
     * Lấy ID của người dùng từ Authentication (JWT)
     * → Email được lưu trong JWT sẽ được dùng để truy vấn thông tin user trong database.
     *
     * @param authentication Thông tin xác thực hiện tại
     * @return userId của người dùng đang đăng nhập
     */
    private Long getUserIdFromAuthentication(Authentication authentication) {
        // The JWT token contains email as subject, so we get the email and find the user
        String email = authentication.getName();
        return authService.getUserByEmail(email).getUserId();
    }
}

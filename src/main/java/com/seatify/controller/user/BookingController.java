package com.seatify.controller.user;

import com.seatify.dto.user.request.UserBookingRequest;
import com.seatify.dto.user.response.UserBookingResponse;
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

@RestController
@RequestMapping("/api/v1/user/bookings")
@RequiredArgsConstructor
@Tag(name = "User Booking", description = "APIs for user booking management")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {

    private final BookingService bookingService;
    private final AuthService authService;

    @Operation(summary = "Đặt chỗ cho sự kiện")
    @PostMapping
    public ResponseEntity<UserBookingResponse> createBooking(
            @Valid @RequestBody UserBookingRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        UserBookingResponse response = bookingService.createBooking(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Lấy danh sách đặt chỗ của user")
    @GetMapping
    public ResponseEntity<List<UserBookingResponse>> getUserBookings(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<UserBookingResponse> bookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    @Operation(summary = "Lấy chi tiết đặt chỗ theo ID")
    @GetMapping("/{bookingId}")
    public ResponseEntity<UserBookingResponse> getBookingById(
            @PathVariable Long bookingId,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        UserBookingResponse booking = bookingService.getBookingById(bookingId, userId);
        return ResponseEntity.ok(booking);
    }

    @Operation(summary = "Hủy đặt chỗ")
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<UserBookingResponse> cancelBooking(
            @PathVariable Long bookingId,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        UserBookingResponse booking = bookingService.cancelBooking(bookingId, userId);
        return ResponseEntity.ok(booking);
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        // The JWT token contains email as subject, so we get the email and find the user
        String email = authentication.getName();
        return authService.getUserByEmail(email).getUserId();
    }
}

package com.seatify.service.user;

import com.seatify.dto.user.request.CheckInRequest;
import com.seatify.dto.user.response.CheckInResponse;
import com.seatify.exception.ResourceNotFoundException;
import com.seatify.exception.ValidationException;
import com.seatify.model.AttendanceLog;
import com.seatify.model.Booking;
import com.seatify.model.constants.AttendanceAction;
import com.seatify.model.constants.BookingStatus;
import com.seatify.model.constants.EventStatus;
import com.seatify.repository.AttendanceLogRepository;
import com.seatify.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Service xử lý check-in và checkout tự động
 * Logic: 
 * - Chỉ cho phép check-in trước 15 phút khi sự kiện bắt đầu
 * - Nếu thời gian check-in < 5 giây, tự động checkout
 * 
 * @author : Lê Văn Nguyễn - CE181235
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final BookingRepository bookingRepository;
    private final AttendanceLogRepository attendanceLogRepository;
    private static final long AUTO_CHECKOUT_THRESHOLD_SECONDS = 5;
    private static final long CHECK_IN_ALLOWED_MINUTES_BEFORE = 15; // Cho phép check-in trước 15 phút

    /**
     * Xử lý check-in hoặc checkout từ QR code
     * QR code format: SEATIFY:seatId:userId:eventId:UUID
     * 
     * Logic:
     * - Chỉ cho phép check-in trước 15 phút khi sự kiện bắt đầu
     * - Nếu chưa check-in: Thực hiện check-in
     * - Nếu đã check-in nhưng chưa checkout: Tự động checkout (toggle)
     *   + Nếu thời gian check-in < 5s: Đánh dấu autoCheckedOut = true (coi như check-in nhầm)
     * - Nếu đã checkout: Thực hiện check-in lại (toggle, vẫn phải tuân thủ quy tắc 15 phút)
     */
    @Transactional
    public CheckInResponse processCheckIn(CheckInRequest request) {
        // Parse QR code data
        ParsedQRCode qrData = parseQRCodeData(request.getQrCodeData());
        
        // Tìm booking từ QR code data
        Booking booking = bookingRepository
                .findBySeatIdAndUserIdAndEventId(qrData.seatId, qrData.userId, qrData.eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found for this QR code"));

        // Validate booking status
        if (booking.getStatus() != BookingStatus.BOOKED) {
            throw new ValidationException("Booking is not in BOOKED status");
        }

        // Validate event status - cho phép check-in khi UPCOMING hoặc ONGOING
        EventStatus eventStatus = booking.getEvent().getStatus();
        if (eventStatus != EventStatus.UPCOMING && eventStatus != EventStatus.ONGOING) {
            throw new ValidationException("Event is not available for check-in");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime eventStartTime = booking.getEvent().getStartTime();
        
        // Validate: Chỉ cho phép check-in trước 15 phút khi sự kiện bắt đầu
        LocalDateTime allowedCheckInStartTime = eventStartTime.minusMinutes(CHECK_IN_ALLOWED_MINUTES_BEFORE);
        if (now.isBefore(allowedCheckInStartTime)) {
            throw new ValidationException(String.format(
                "Chỉ có thể check-in trước 15 phút khi sự kiện bắt đầu. Sự kiện bắt đầu lúc %s, bạn có thể check-in từ %s",
                eventStartTime.toString(),
                allowedCheckInStartTime.toString()
            ));
        }
        boolean autoCheckedOut = false;

        // Xử lý check-in hoặc checkout
        if (booking.getCheckInTime() == null) {
            // Chưa check-in: Thực hiện check-in
            booking.setCheckInTime(now);
            booking.setCheckOutTime(null); // Reset checkout time if exists
            booking = bookingRepository.save(booking);
            
            // Log check-in
            AttendanceLog checkInLog = AttendanceLog.builder()
                    .booking(booking)
                    .action(AttendanceAction.CHECK_IN)
                    .timestamp(now)
                    .build();
            attendanceLogRepository.save(checkInLog);

            log.info("User {} checked in for event {} at {}", 
                    qrData.userId, qrData.eventId, now);

            return CheckInResponse.builder()
                    .bookingId(booking.getBookingId())
                    .eventId(booking.getEvent().getEventId())
                    .eventName(booking.getEvent().getEventName())
                    .seatLabel(booking.getSeat().getSeatRow() + booking.getSeat().getSeatNumber())
                    .action(AttendanceAction.CHECK_IN)
                    .timestamp(now)
                    .message("Check-in thành công")
                    .autoCheckedOut(false)
                    .build();

        } else if (booking.getCheckOutTime() == null) {
            // Đã check-in nhưng chưa checkout: Tự động checkout
            LocalDateTime checkInTime = booking.getCheckInTime();
            Duration duration = Duration.between(checkInTime, now);
            long secondsSinceCheckIn = duration.getSeconds();

            booking.setCheckOutTime(now);
            booking = bookingRepository.save(booking);

            // Kiểm tra nếu thời gian check-in < 5s thì đánh dấu là auto checkout
            if (secondsSinceCheckIn < AUTO_CHECKOUT_THRESHOLD_SECONDS) {
                autoCheckedOut = true;
                log.info("Auto checkout for user {} in event {} - check-in duration was {}s (threshold: {}s)", 
                        qrData.userId, qrData.eventId, secondsSinceCheckIn, AUTO_CHECKOUT_THRESHOLD_SECONDS);
            }

            // Log checkout
            AttendanceLog checkoutLog = AttendanceLog.builder()
                    .booking(booking)
                    .action(AttendanceAction.CHECK_OUT)
                    .timestamp(now)
                    .build();
            attendanceLogRepository.save(checkoutLog);

            log.info("User {} checked out for event {} at {}", 
                    qrData.userId, qrData.eventId, now);

            String message = secondsSinceCheckIn < AUTO_CHECKOUT_THRESHOLD_SECONDS 
                ? "Đã tự động checkout do thời gian check-in quá ngắn (" + secondsSinceCheckIn + " giây)"
                : "Checkout thành công";

            return CheckInResponse.builder()
                    .bookingId(booking.getBookingId())
                    .eventId(booking.getEvent().getEventId())
                    .eventName(booking.getEvent().getEventName())
                    .seatLabel(booking.getSeat().getSeatRow() + booking.getSeat().getSeatNumber())
                    .action(AttendanceAction.CHECK_OUT)
                    .timestamp(now)
                    .message(message)
                    .autoCheckedOut(autoCheckedOut)
                    .build();
        } else {
            // Đã checkout: Cho phép check-in lại
            booking.setCheckInTime(now);
            booking.setCheckOutTime(null);
            booking = bookingRepository.save(booking);

            // Log check-in lại
            AttendanceLog checkInLog = AttendanceLog.builder()
                    .booking(booking)
                    .action(AttendanceAction.CHECK_IN)
                    .timestamp(now)
                    .build();
            attendanceLogRepository.save(checkInLog);

            log.info("User {} checked in again for event {} at {}", 
                    qrData.userId, qrData.eventId, now);

            return CheckInResponse.builder()
                    .bookingId(booking.getBookingId())
                    .eventId(booking.getEvent().getEventId())
                    .eventName(booking.getEvent().getEventName())
                    .seatLabel(booking.getSeat().getSeatRow() + booking.getSeat().getSeatNumber())
                    .action(AttendanceAction.CHECK_IN)
                    .timestamp(now)
                    .message("Check-in lại thành công")
                    .autoCheckedOut(false)
                    .build();
        }
    }

    /**
     * Xử lý checkout từ QR code
     * QR code format: SEATIFY:seatId:userId:eventId:UUID
     * 
     * Logic:
     * - Phải đã check-in mới có thể checkout
     * - Nếu thời gian check-in < 5s: Tự động checkout (coi như check-in nhầm)
     * - Nếu thời gian check-in >= 5s: Checkout bình thường
     */
    @Transactional
    public CheckInResponse processCheckout(CheckInRequest request) {
        // Parse QR code data
        ParsedQRCode qrData = parseQRCodeData(request.getQrCodeData());
        
        // Tìm booking từ QR code data
        Booking booking = bookingRepository
                .findBySeatIdAndUserIdAndEventId(qrData.seatId, qrData.userId, qrData.eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found for this QR code"));

        // Validate booking status
        if (booking.getStatus() != BookingStatus.BOOKED) {
            throw new ValidationException("Booking is not in BOOKED status");
        }

        // Validate event status - checkout chỉ cho phép khi event đang diễn ra hoặc đã kết thúc
        EventStatus eventStatus = booking.getEvent().getStatus();
        if (eventStatus != EventStatus.ONGOING && eventStatus != EventStatus.FINISHED) {
            throw new ValidationException("Event is not available for checkout");
        }

        // Phải đã check-in mới có thể checkout
        if (booking.getCheckInTime() == null) {
            throw new ValidationException("Cannot checkout: User has not checked in yet");
        }

        // Đã checkout rồi
        if (booking.getCheckOutTime() != null) {
            throw new ValidationException("Already checked out");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime checkInTime = booking.getCheckInTime();
        Duration duration = Duration.between(checkInTime, now);
        long secondsSinceCheckIn = duration.getSeconds();
        boolean autoCheckedOut = false;

        if (secondsSinceCheckIn < AUTO_CHECKOUT_THRESHOLD_SECONDS) {
            // Thời gian check-in < 5s: Tự động checkout (coi như check-in nhầm)
            booking.setCheckOutTime(now);
            booking = bookingRepository.save(booking);
            autoCheckedOut = true;

            // Log checkout tự động
            AttendanceLog checkoutLog = AttendanceLog.builder()
                    .booking(booking)
                    .action(AttendanceAction.CHECK_OUT)
                    .timestamp(now)
                    .build();
            attendanceLogRepository.save(checkoutLog);

            log.info("Auto checkout for user {} in event {} - check-in duration was {}s (threshold: {}s)", 
                    qrData.userId, qrData.eventId, secondsSinceCheckIn, AUTO_CHECKOUT_THRESHOLD_SECONDS);

            return CheckInResponse.builder()
                    .bookingId(booking.getBookingId())
                    .eventId(booking.getEvent().getEventId())
                    .eventName(booking.getEvent().getEventName())
                    .seatLabel(booking.getSeat().getSeatRow() + booking.getSeat().getSeatNumber())
                    .action(AttendanceAction.CHECK_OUT)
                    .timestamp(now)
                    .message("Đã tự động checkout do thời gian check-in quá ngắn (" + secondsSinceCheckIn + " giây)")
                    .autoCheckedOut(true)
                    .build();
        } else {
            // Thời gian check-in >= 5s: Checkout bình thường
            booking.setCheckOutTime(now);
            booking = bookingRepository.save(booking);

            // Log checkout
            AttendanceLog checkoutLog = AttendanceLog.builder()
                    .booking(booking)
                    .action(AttendanceAction.CHECK_OUT)
                    .timestamp(now)
                    .build();
            attendanceLogRepository.save(checkoutLog);

            log.info("User {} checked out for event {} at {}", 
                    qrData.userId, qrData.eventId, now);

            return CheckInResponse.builder()
                    .bookingId(booking.getBookingId())
                    .eventId(booking.getEvent().getEventId())
                    .eventName(booking.getEvent().getEventName())
                    .seatLabel(booking.getSeat().getSeatRow() + booking.getSeat().getSeatNumber())
                    .action(AttendanceAction.CHECK_OUT)
                    .timestamp(now)
                    .message("Checkout thành công")
                    .autoCheckedOut(false)
                    .build();
        }
    }

    /**
     * Parse QR code data từ format: SEATIFY:seatId:userId:eventId:UUID
     */
    private ParsedQRCode parseQRCodeData(String qrCodeData) {
        if (qrCodeData == null || qrCodeData.isEmpty()) {
            throw new ValidationException("QR code data is empty");
        }

        String[] parts = qrCodeData.split(":");
        if (parts.length < 4 || !parts[0].equals("SEATIFY")) {
            throw new ValidationException("Invalid QR code format");
        }

        try {
            Long seatId = Long.parseLong(parts[1]);
            Long userId = Long.parseLong(parts[2]);
            Long eventId = Long.parseLong(parts[3]);

            return new ParsedQRCode(seatId, userId, eventId);
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid QR code data format");
        }
    }

    /**
     * Helper class để lưu thông tin parsed từ QR code
     */
    private static class ParsedQRCode {
        final Long seatId;
        final Long userId;
        final Long eventId;

        ParsedQRCode(Long seatId, Long userId, Long eventId) {
            this.seatId = seatId;
            this.userId = userId;
            this.eventId = eventId;
        }
    }
}

package com.seatify.service.admin;

import com.seatify.dto.admin.request.AdminBookingBulkStatusUpdateDTO;
import com.seatify.dto.admin.request.AdminBookingStatusUpdateDTO;
import com.seatify.dto.admin.response.AdminBookingResponseDTO;
import com.seatify.model.Booking;
import com.seatify.model.constants.BookingStatus;
import com.seatify.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminBookingServiceImpl implements AdminBookingService {
    private final BookingRepository bookingRepository;

    @Override
    public Page<AdminBookingResponseDTO> getBookings(String search, String status, Long eventId, int page, int size, String sortBy, boolean desc) {
        Sort sort = Sort.by(desc ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        if ((search == null || search.isEmpty()) && (status == null || status.equalsIgnoreCase("ALL")) && eventId == null) {
            List<Booking> all = bookingRepository.findAllWithDetails();
            int total = all.size();
            int from = (int)pageable.getOffset();
            int to = Math.min(from + pageable.getPageSize(), total);
            List<AdminBookingResponseDTO> content = all.stream().skip(from).limit(to-from).map(this::toDto).toList();
            return new org.springframework.data.domain.PageImpl<>(content, pageable, total);
        }
        // Có filter, dùng lại cách cũ (sẽ bổ sung nếu cần)
        return bookingRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    public AdminBookingResponseDTO getBookingDetail(Long bookingId) {
        return null;
    }

    @Override
    @Transactional
    public AdminBookingResponseDTO updateBookingStatus(Long bookingId, AdminBookingStatusUpdateDTO dto) {
        return null;
    }

    @Override
    @Transactional
    public List<AdminBookingResponseDTO> bulkUpdateStatus(AdminBookingBulkStatusUpdateDTO dto) {
        return List.of();
    }

    @Override
    public void deleteBooking(Long bookingId) {
    }

    private AdminBookingResponseDTO toDto(Booking booking) {
        return AdminBookingResponseDTO.builder()
                .bookingId(booking.getBookingId())
                .user(AdminBookingResponseDTO.UserInfo.builder()
                        .userId(booking.getUser().getUserId())
                        .fullName(booking.getUser().getFullName())
                        .email(booking.getUser().getEmail())
                        .mssv(booking.getUser().getMssv())
                        .phone(booking.getUser().getPhone())
                        .build())
                .event(AdminBookingResponseDTO.EventInfo.builder()
                        .eventId(booking.getEvent().getEventId())
                        .eventName(booking.getEvent().getEventName())
                        .location(booking.getEvent().getLocation())
                        .startTime(booking.getEvent().getStartTime())
                        .endTime(booking.getEvent().getEndTime())
                        .build())
                .seat(AdminBookingResponseDTO.SeatInfo.builder()
                        .seatId(booking.getSeat().getSeatId())
                        .seatRow(booking.getSeat().getSeatRow())
                        .seatNumber(booking.getSeat().getSeatNumber())
                        .build())
                .status(booking.getStatus())
                .bookingTime(booking.getBookingTime())
                .checkInTime(booking.getCheckInTime())
                .checkOutTime(booking.getCheckOutTime())
                .build();
    }
}

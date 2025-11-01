package com.seatify.service.admin;

import com.seatify.dto.admin.request.AdminBookingBulkStatusUpdateDTO;
import com.seatify.dto.admin.request.AdminBookingStatusUpdateDTO;
import com.seatify.dto.admin.response.AdminBookingResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AdminBookingService {
    Page<AdminBookingResponseDTO> getBookings(String search, String status, Long eventId, int page, int size, String sortBy, boolean desc);
    AdminBookingResponseDTO getBookingDetail(Long bookingId);
    AdminBookingResponseDTO updateBookingStatus(Long bookingId, AdminBookingStatusUpdateDTO dto);
    List<AdminBookingResponseDTO> bulkUpdateStatus(AdminBookingBulkStatusUpdateDTO dto);
    void deleteBooking(Long bookingId);
}

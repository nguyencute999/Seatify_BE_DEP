package com.seatify.controller.admin;

import com.seatify.dto.admin.response.DashboardStatsDTO;
import com.seatify.dto.admin.response.RecentBookingDTO;
import com.seatify.dto.admin.response.RecentEventDTO;
import com.seatify.service.admin.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller admin cho dashboard.
 * Cung cấp các API để lấy thống kê và danh sách gần đây.
 * 
 * @author : Lê Văn Nguyễn - CE181235
 */
@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {
    
    private final AdminDashboardService dashboardService;
    
    /**
     * Lấy thống kê tổng quan cho dashboard.
     * 
     * @return DashboardStatsDTO chứa các số liệu thống kê
     */
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getStats() {
        DashboardStatsDTO stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Lấy danh sách đặt chỗ gần đây.
     * 
     * @param limit Số lượng bản ghi muốn lấy (mặc định 10)
     * @return Danh sách đặt chỗ gần đây
     */
    @GetMapping("/recent-bookings")
    public ResponseEntity<List<RecentBookingDTO>> getRecentBookings(
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<RecentBookingDTO> bookings = dashboardService.getRecentBookings(limit);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Lấy danh sách sự kiện gần đây.
     * 
     * @param limit Số lượng bản ghi muốn lấy (mặc định 10)
     * @return Danh sách sự kiện gần đây
     */
    @GetMapping("/recent-events")
    public ResponseEntity<List<RecentEventDTO>> getRecentEvents(
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<RecentEventDTO> events = dashboardService.getRecentEvents(limit);
        return ResponseEntity.ok(events);
    }
}


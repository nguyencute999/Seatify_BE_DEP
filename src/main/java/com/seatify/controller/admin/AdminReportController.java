package com.seatify.controller.admin;

import com.seatify.dto.admin.response.*;
import com.seatify.service.admin.AdminReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller admin cho báo cáo thống kê.
 * Cung cấp các API để lấy các loại báo cáo khác nhau.
 * 
 * @author : Lê Văn Nguyễn - CE181235
 */
@RestController
@RequestMapping("/api/v1/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {
    
    private final AdminReportService reportService;
    
    /**
     * Lấy thống kê tổng quan.
     * 
     * @param startDate Ngày bắt đầu (format: yyyy-MM-dd)
     * @param endDate Ngày kết thúc (format: yyyy-MM-dd)
     * @param eventId ID sự kiện (tùy chọn, null nếu lấy tất cả)
     * @return OverviewStatsDTO
     */
    @GetMapping("/overview")
    public ResponseEntity<OverviewStatsDTO> getOverviewStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long eventId
    ) {
        OverviewStatsDTO stats = reportService.getOverviewStats(startDate, endDate, eventId);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Lấy thống kê sự kiện.
     * 
     * @param startDate Ngày bắt đầu (format: yyyy-MM-dd)
     * @param endDate Ngày kết thúc (format: yyyy-MM-dd)
     * @param eventId ID sự kiện (tùy chọn, null nếu lấy tất cả)
     * @return Danh sách EventStatsDTO
     */
    @GetMapping("/events")
    public ResponseEntity<List<EventStatsDTO>> getEventStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long eventId
    ) {
        List<EventStatsDTO> stats = reportService.getEventStats(startDate, endDate, eventId);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Lấy thống kê người dùng theo tháng.
     * 
     * @param startDate Ngày bắt đầu (format: yyyy-MM-dd)
     * @param endDate Ngày kết thúc (format: yyyy-MM-dd)
     * @return Danh sách UserStatsDTO
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserStatsDTO>> getUserStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<UserStatsDTO> stats = reportService.getUserStats(startDate, endDate);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Lấy xu hướng đặt chỗ theo ngày.
     * 
     * @param startDate Ngày bắt đầu (format: yyyy-MM-dd)
     * @param endDate Ngày kết thúc (format: yyyy-MM-dd)
     * @return Danh sách BookingTrendDTO
     */
    @GetMapping("/booking-trends")
    public ResponseEntity<List<BookingTrendDTO>> getBookingTrends(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<BookingTrendDTO> trends = reportService.getBookingTrends(startDate, endDate);
        return ResponseEntity.ok(trends);
    }
    
    /**
     * Lấy dữ liệu tỷ lệ tham gia theo sự kiện.
     * 
     * @param startDate Ngày bắt đầu (format: yyyy-MM-dd)
     * @param endDate Ngày kết thúc (format: yyyy-MM-dd)
     * @param eventId ID sự kiện (tùy chọn, null nếu lấy tất cả)
     * @return Danh sách AttendanceDataDTO
     */
    @GetMapping("/attendance")
    public ResponseEntity<List<AttendanceDataDTO>> getAttendanceData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long eventId
    ) {
        List<AttendanceDataDTO> data = reportService.getAttendanceData(startDate, endDate, eventId);
        return ResponseEntity.ok(data);
    }
    
    /**
     * Lấy thống kê doanh thu theo sự kiện.
     * 
     * @param startDate Ngày bắt đầu (format: yyyy-MM-dd)
     * @param endDate Ngày kết thúc (format: yyyy-MM-dd)
     * @param eventId ID sự kiện (tùy chọn, null nếu lấy tất cả)
     * @return Danh sách RevenueStatsDTO
     */
    @GetMapping("/revenue")
    public ResponseEntity<List<RevenueStatsDTO>> getRevenueStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long eventId
    ) {
        List<RevenueStatsDTO> stats = reportService.getRevenueStats(startDate, endDate, eventId);
        return ResponseEntity.ok(stats);
    }
}


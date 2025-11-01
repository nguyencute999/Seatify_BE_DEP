package com.seatify.service.admin;

import com.seatify.dto.admin.response.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Service interface cho báo cáo admin.
 * 
 * @author : Lê Văn Nguyễn - CE181235
 */
public interface AdminReportService {
    /**
     * Lấy thống kê tổng quan.
     * 
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @param eventId ID sự kiện (null nếu lấy tất cả)
     * @return OverviewStatsDTO
     */
    OverviewStatsDTO getOverviewStats(LocalDate startDate, LocalDate endDate, Long eventId);
    
    /**
     * Lấy thống kê sự kiện.
     * 
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @param eventId ID sự kiện (null nếu lấy tất cả)
     * @return Danh sách EventStatsDTO
     */
    List<EventStatsDTO> getEventStats(LocalDate startDate, LocalDate endDate, Long eventId);
    
    /**
     * Lấy thống kê người dùng theo tháng.
     * 
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @return Danh sách UserStatsDTO
     */
    List<UserStatsDTO> getUserStats(LocalDate startDate, LocalDate endDate);
    
    /**
     * Lấy xu hướng đặt chỗ theo ngày.
     * 
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @return Danh sách BookingTrendDTO
     */
    List<BookingTrendDTO> getBookingTrends(LocalDate startDate, LocalDate endDate);
    
    /**
     * Lấy dữ liệu tỷ lệ tham gia theo sự kiện.
     * 
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @param eventId ID sự kiện (null nếu lấy tất cả)
     * @return Danh sách AttendanceDataDTO
     */
    List<AttendanceDataDTO> getAttendanceData(LocalDate startDate, LocalDate endDate, Long eventId);
    
    /**
     * Lấy thống kê doanh thu theo sự kiện.
     * 
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @param eventId ID sự kiện (null nếu lấy tất cả)
     * @return Danh sách RevenueStatsDTO
     */
    List<RevenueStatsDTO> getRevenueStats(LocalDate startDate, LocalDate endDate, Long eventId);
}


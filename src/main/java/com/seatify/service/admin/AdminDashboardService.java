package com.seatify.service.admin;

import com.seatify.dto.admin.response.DashboardStatsDTO;
import com.seatify.dto.admin.response.RecentBookingDTO;
import com.seatify.dto.admin.response.RecentEventDTO;

import java.util.List;

/**
 * Service interface cho dashboard admin.
 * 
 * @author : Lê Văn Nguyễn - CE181235
 */
public interface AdminDashboardService {
    /**
     * Lấy thống kê tổng quan cho dashboard.
     * 
     * @return DashboardStatsDTO chứa các số liệu thống kê
     */
    DashboardStatsDTO getDashboardStats();
    
    /**
     * Lấy danh sách đặt chỗ gần đây.
     * 
     * @param limit Số lượng bản ghi muốn lấy (mặc định 10)
     * @return Danh sách đặt chỗ gần đây
     */
    List<RecentBookingDTO> getRecentBookings(int limit);
    
    /**
     * Lấy danh sách sự kiện gần đây.
     * 
     * @param limit Số lượng bản ghi muốn lấy (mặc định 10)
     * @return Danh sách sự kiện gần đây
     */
    List<RecentEventDTO> getRecentEvents(int limit);
}


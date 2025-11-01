package com.seatify.dto.admin.response;

import lombok.Builder;
import lombok.Data;

/**
 * DTO cho thống kê tổng quan báo cáo.
 * 
 * @author : Lê Văn Nguyễn - CE181235
 */
@Data
@Builder
public class OverviewStatsDTO {
    private Long totalEvents;
    private Long totalBookings;
    private Long totalUsers;
    private Long totalRevenue;
    private Double averageAttendance;
    private String topEvent; // Tên sự kiện có nhiều booking nhất
}


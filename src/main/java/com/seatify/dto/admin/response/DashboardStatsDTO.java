package com.seatify.dto.admin.response;

import lombok.Builder;
import lombok.Data;

/**
 * DTO chứa thống kê tổng quan cho dashboard admin.
 * 
 * @author : Lê Văn Nguyễn - CE181235
 */
@Data
@Builder
public class DashboardStatsDTO {
    private Long totalUsers;
    private Long totalEvents;
    private Long totalBookings;
    private Long totalRevenue; // VND
    private Long activeEvents; // Events với status ONGOING
    private Long upcomingEvents; // Events với status UPCOMING
    private Long completedEvents; // Events với status FINISHED
    private Long cancelledEvents; // Events với status CANCELLED
}


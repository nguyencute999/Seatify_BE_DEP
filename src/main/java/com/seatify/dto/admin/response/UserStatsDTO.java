package com.seatify.dto.admin.response;

import lombok.Builder;
import lombok.Data;

/**
 * DTO cho thống kê người dùng theo tháng.
 * 
 * @author : Lê Văn Nguyễn - CE181235
 */
@Data
@Builder
public class UserStatsDTO {
    private String month; // Format: YYYY-MM
    private Long newUsers;
    private Long activeUsers; // Người dùng có booking trong tháng
    private Long totalBookings;
    private Double growthRate; // Tỷ lệ tăng trưởng so với tháng trước (%)
}


package com.seatify.dto.admin.response;

import lombok.Builder;
import lombok.Data;

/**
 * DTO cho thống kê doanh thu theo sự kiện.
 * 
 * @author : Lê Văn Nguyễn - CE181235
 */
@Data
@Builder
public class RevenueStatsDTO {
    private String eventName;
    private Long revenue;
    private Double percentage; // Tỷ lệ phần trăm so với tổng doanh thu cao nhất
}


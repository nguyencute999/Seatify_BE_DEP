package com.seatify.dto.admin.response;

import lombok.Builder;
import lombok.Data;

/**
 * DTO cho dữ liệu tỷ lệ tham gia theo sự kiện.
 * 
 * @author : Lê Văn Nguyễn - CE181235
 */
@Data
@Builder
public class AttendanceDataDTO {
    private String eventName;
    private Double attendance; // Tỷ lệ tham gia (%)
}


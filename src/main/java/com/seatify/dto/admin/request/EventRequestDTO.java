package com.seatify.dto.admin.request;

import com.seatify.model.constants.EventStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO cho việc tạo hoặc cập nhật sự kiện trong hệ thống quản trị.
 * Dùng để nhận dữ liệu từ client và xác thực đầu vào.
 *
 * @author : Lê Văn Nguyễn - CE181235
 */
@Data
public class EventRequestDTO {
    @NotBlank(message = "Tên sự kiện không được để trống")
    private String eventName;
    
    @NotBlank(message = "Mô tả không được để trống")
    private String description;
    
    @NotBlank(message = "Địa điểm không được để trống")
    private String location;
    
    @NotNull(message = "Thời gian bắt đầu không được để trống")
    private LocalDateTime startTime;
    
    @NotNull(message = "Thời gian kết thúc không được để trống")
    private LocalDateTime endTime;
    
    @NotNull(message = "Sức chứa không được để trống")
    @Positive(message = "Sức chứa phải lớn hơn 0")
    private Integer capacity;
    
    @NotNull(message = "Số hàng ghế không được để trống")
    @Positive(message = "Số hàng ghế phải lớn hơn 0")
    private Integer seatRows;
    
    @NotNull(message = "Số ghế mỗi hàng không được để trống")
    @Positive(message = "Số ghế mỗi hàng phải lớn hơn 0")
    private Integer seatsPerRow;
    
    private EventStatus status;
    private String thumbnail;

    /**
     * Kiểm tra tính hợp lệ của sự kiện:
     * - Thời gian bắt đầu phải trước thời gian kết thúc.
     * - Sức chứa phải bằng số hàng ghế * số ghế mỗi hàng.
     *
     * @return true nếu hợp lệ, false nếu sai điều kiện.
     */
    public boolean isValid() {
        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            return false;
        }
        if (seatRows != null && seatsPerRow != null && capacity != null) {
            return (seatRows * seatsPerRow) == capacity;
        }
        return true;
    }
}

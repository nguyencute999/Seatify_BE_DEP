package com.seatify.dto.admin.request;

import com.seatify.model.constants.EventStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

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
    
    // Custom validation method
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

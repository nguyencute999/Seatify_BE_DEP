package com.seatify.service;

import com.seatify.dto.response.SeatResponseDTO;
import com.seatify.model.Event;

import java.util.List;

/**
 * @author : Lê Văn Nguyễn - CE181235
 */
public interface SeatService {
    /**
     * Tạo ghế tự động cho sự kiện
     * @param event Sự kiện cần tạo ghế
     * @param seatRows Số hàng ghế
     * @param seatsPerRow Số ghế mỗi hàng
     * @return Danh sách ghế đã tạo
     */
    List<SeatResponseDTO> generateSeatsForEvent(Event event, Integer seatRows, Integer seatsPerRow);
    
    /**
     * Lấy danh sách ghế theo sự kiện
     * @param eventId ID của sự kiện
     * @return Danh sách ghế
     */
    List<SeatResponseDTO> getSeatsByEventId(Long eventId);
}

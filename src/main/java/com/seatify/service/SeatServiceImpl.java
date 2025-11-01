package com.seatify.service;

import com.seatify.dto.response.SeatResponseDTO;
import com.seatify.exception.ResourceNotFoundException;
import com.seatify.model.Event;
import com.seatify.model.Seat;
import com.seatify.repository.EventRepository;
import com.seatify.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Triển khai logic nghiệp vụ liên quan đến ghế ngồi (Seat) trong hệ thống Seatify.
 * Cung cấp các chức năng như tạo ghế cho sự kiện và lấy danh sách ghế theo sự kiện.
 *
 * Mỗi ghế được gắn với một sự kiện cụ thể và có các thuộc tính như hàng ghế, số ghế và trạng thái khả dụng.
 *
 * @author : Lê Văn Nguyễn - CE181235
 */
@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final EventRepository eventRepository;

    /**
     * Tạo danh sách ghế tự động cho một sự kiện.
     * - Số hàng ghế được đặt theo ký tự (A, B, C, ...).
     * - Mỗi hàng chứa số ghế được chỉ định.
     * - Mặc định, tất cả ghế đều khả dụng (isAvailable = true).
     *
     * @param event       Sự kiện cần tạo ghế.
     * @param seatRows    Số hàng ghế.
     * @param seatsPerRow Số ghế mỗi hàng.
     * @return Danh sách {@link SeatResponseDTO} của các ghế được tạo.
     */
    @Override
    @Transactional
    public List<SeatResponseDTO> generateSeatsForEvent(Event event, Integer seatRows, Integer seatsPerRow) {
        List<Seat> seats = new ArrayList<>();
        
        // Tạo ghế theo hàng (A, B, C, ...) và số ghế (1, 2, 3, ...)
        for (int row = 0; row < seatRows; row++) {
            String seatRow = String.valueOf((char) ('A' + row));
            
            for (int seatNumber = 1; seatNumber <= seatsPerRow; seatNumber++) {
                Seat seat = Seat.builder()
                        .event(event)
                        .seatRow(seatRow)
                        .seatNumber(seatNumber)
                        .isAvailable(true)
                        .build();
                
                seats.add(seat);
            }
        }
        
        // save seat
        List<Seat> savedSeats = seatRepository.saveAll(seats);
        
        //chuyển sang dto
        return savedSeats.stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Lấy danh sách ghế của một sự kiện theo ID.
     * Nếu sự kiện không tồn tại, ném ra {@link ResourceNotFoundException}.
     *
     * @param eventId ID của sự kiện.
     * @return Danh sách {@link SeatResponseDTO} thuộc về sự kiện.
     */
    @Override
    public List<SeatResponseDTO> getSeatsByEventId(Long eventId) {
        // Kiểm tra sự kiện có tồn tại không
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        
        List<Seat> seats = seatRepository.findByEvent(event);
        
        return seats.stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Chuyển đổi từ entity {@link Seat} sang {@link SeatResponseDTO}.
     *
     * @param seat Đối tượng ghế cần chuyển đổi.
     * @return Đối tượng DTO chứa thông tin ghế.
     */
    private SeatResponseDTO convertToResponseDTO(Seat seat) {
        return SeatResponseDTO.builder()
                .seatId(seat.getSeatId())
                .seatRow(seat.getSeatRow())
                .seatNumber(seat.getSeatNumber())
                .isAvailable(seat.getIsAvailable())
                .eventId(seat.getEvent().getEventId())
                .build();
    }
}

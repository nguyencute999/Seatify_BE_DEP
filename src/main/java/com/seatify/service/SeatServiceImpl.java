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

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final EventRepository eventRepository;

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
        
        // Lưu tất cả ghế vào database
        List<Seat> savedSeats = seatRepository.saveAll(seats);
        
        // Chuyển đổi sang DTO
        return savedSeats.stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

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

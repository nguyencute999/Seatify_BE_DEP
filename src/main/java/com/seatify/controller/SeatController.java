package com.seatify.controller;

import com.seatify.dto.response.SeatResponseDTO;
import com.seatify.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    /**
     * Lấy danh sách ghế theo sự kiện
     * @param eventId ID của sự kiện
     * @return Danh sách ghế
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<SeatResponseDTO>> getSeatsByEventId(@PathVariable Long eventId) {
        List<SeatResponseDTO> seats = seatService.getSeatsByEventId(eventId);
        return ResponseEntity.ok(seats);
    }
}
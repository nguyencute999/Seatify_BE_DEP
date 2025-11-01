package com.seatify.controller;

import com.seatify.dto.response.SeatResponseDTO;
import com.seatify.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Author: Lê Văn Nguyễn - CE181235
 * Controller quản lý api liên quan đến ghế
 */
@RestController
@RequestMapping("/api/v1/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    /**
     * Lấy danh sách ghế theo ID sự kiện
     *
     * @param eventId ID của sự kiện
     * @return Danh sách ghế thuộc sự kiện
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<SeatResponseDTO>> getSeatsByEventId(@PathVariable Long eventId) {
        List<SeatResponseDTO> seats = seatService.getSeatsByEventId(eventId);
        return ResponseEntity.ok(seats);
    }
}
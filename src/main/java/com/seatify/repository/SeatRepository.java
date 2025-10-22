package com.seatify.repository;

import com.seatify.model.Event;
import com.seatify.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByEvent(Event event);
    long countByEventAndIsAvailableTrue(Event event);
}



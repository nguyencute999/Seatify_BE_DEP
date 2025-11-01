package com.seatify.repository;

import com.seatify.model.Event;
import com.seatify.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author : Lê Văn Nguyễn - CE181235
 */
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByEvent(Event event);
    long countByEventAndIsAvailableTrue(Event event);
    
    @Query("SELECT s FROM Seat s WHERE s.event.eventId = :eventId AND s.isAvailable = true")
    List<Seat> findAvailableSeatsByEventId(@Param("eventId") Long eventId);
    
    @Query("SELECT s FROM Seat s WHERE s.seatId = :seatId AND s.event.eventId = :eventId AND s.isAvailable = true")
    Optional<Seat> findAvailableSeatByIdAndEventId(@Param("seatId") Long seatId, @Param("eventId") Long eventId);
}



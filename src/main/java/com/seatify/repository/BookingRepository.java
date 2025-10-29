package com.seatify.repository;

import com.seatify.model.Booking;
import com.seatify.model.Event;
import com.seatify.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByUserAndEvent(User user, Event event);
    boolean existsByUserAndEvent(User user, Event event);
    
    @Query("SELECT b FROM Booking b WHERE b.user.userId = :userId")
    List<Booking> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT b FROM Booking b WHERE b.event.eventId = :eventId")
    List<Booking> findByEventId(@Param("eventId") Long eventId);
}



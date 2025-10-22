package com.seatify.repository;

import com.seatify.model.Booking;
import com.seatify.model.Event;
import com.seatify.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByUserAndEvent(User user, Event event);
    boolean existsByUserAndEvent(User user, Event event);
}



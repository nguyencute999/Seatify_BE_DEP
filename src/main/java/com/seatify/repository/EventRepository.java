package com.seatify.repository;

import com.seatify.model.Event;
import com.seatify.model.constants.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(EventStatus status);
    List<Event> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    Page<Event> findByEventNameContainingIgnoreCase(String eventName, Pageable pageable);
}



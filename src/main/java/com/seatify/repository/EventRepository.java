package com.seatify.repository;

import com.seatify.model.Event;
import com.seatify.model.constants.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author : Lê Văn Nguyễn - CE181235
 */
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    List<Event> findByStatus(EventStatus status);
    long countByStatus(EventStatus status);
    List<Event> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    Page<Event> findByEventNameContainingIgnoreCase(String eventName, Pageable pageable);
    
    /**
     * Lấy sự kiện nổi bật dựa trên số lượt booking cao nhất
     * Chỉ lấy sự kiện UPCOMING và chưa bắt đầu
     * Sắp xếp theo số lượng booking giảm dần
     */
    @Query("SELECT e FROM Event e " +
           "LEFT JOIN Booking b ON b.event.eventId = e.eventId AND b.status = 'BOOKED' " +
           "WHERE e.status = :status AND e.startTime > :now " +
           "GROUP BY e.eventId " +
           "ORDER BY COUNT(b.bookingId) DESC")
    List<Event> findFeaturedEventsByBookingCount(
        @Param("status") EventStatus status,
        @Param("now") LocalDateTime now,
        Pageable pageable
    );
    
    @Query("SELECT COUNT(e) FROM Event e WHERE e.startTime >= :startDate AND e.startTime < :endDate")
    long countByStartTimeBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e FROM Event e WHERE e.startTime >= :startDate AND e.startTime < :endDate")
    List<Event> findEventsByStartTimeBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e FROM Event e WHERE (:eventId IS NULL OR e.eventId = :eventId) " +
           "AND e.startTime >= :startDate AND e.startTime < :endDate")
    List<Event> findEventsByEventIdAndStartTimeBetween(
        @Param("eventId") Long eventId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}



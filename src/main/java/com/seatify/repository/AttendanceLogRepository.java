package com.seatify.repository;

import com.seatify.model.AttendanceLog;
import com.seatify.model.Booking;
import com.seatify.model.constants.AttendanceAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author : Lê Văn Nguyễn - CE181235
 */
public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, Long> {
    List<AttendanceLog> findByBooking(Booking booking);
    
    @Query("SELECT COUNT(DISTINCT al.booking.bookingId) FROM AttendanceLog al " +
           "WHERE al.booking.event.eventId = :eventId AND al.action = :action")
    long countDistinctBookingsByEventIdAndAction(
        @Param("eventId") Long eventId,
        @Param("action") AttendanceAction action
    );
    
    @Query("SELECT COUNT(DISTINCT al.booking.bookingId) FROM AttendanceLog al " +
           "WHERE al.booking.event.eventId = :eventId AND al.action = :action " +
           "AND al.timestamp >= :startDate AND al.timestamp < :endDate")
    long countDistinctBookingsByEventIdAndActionAndTimeBetween(
        @Param("eventId") Long eventId,
        @Param("action") AttendanceAction action,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}



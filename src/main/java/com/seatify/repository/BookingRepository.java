package com.seatify.repository;

import com.seatify.model.Booking;
import com.seatify.model.Event;
import com.seatify.model.User;
import com.seatify.model.constants.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author : Lê Văn Nguyễn - CE181235
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByUserAndEvent(User user, Event event);
    boolean existsByUserAndEvent(User user, Event event);
    
    @Query("SELECT b FROM Booking b WHERE b.user.userId = :userId")
    List<Booking> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT b FROM Booking b WHERE b.event.eventId = :eventId")
    List<Booking> findByEventId(@Param("eventId") Long eventId);

    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.user LEFT JOIN FETCH b.event LEFT JOIN FETCH b.seat")
    List<Booking> findAllWithDetails();
    
    @Query("SELECT b FROM Booking b WHERE b.seat.seatId = :seatId AND b.user.userId = :userId AND b.event.eventId = :eventId")
    Optional<Booking> findBySeatIdAndUserIdAndEventId(
        @Param("seatId") Long seatId,
        @Param("userId") Long userId,
        @Param("eventId") Long eventId
    );
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.event.eventId = :eventId AND b.status = :status")
    long countByEventEventIdAndStatus(@Param("eventId") Long eventId, @Param("status") BookingStatus status);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.bookingTime >= :startDate AND b.bookingTime < :endDate")
    long countByBookingTimeBetween(@Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.event.eventId = :eventId " +
           "AND b.bookingTime >= :startDate AND b.bookingTime < :endDate")
    long countByEventIdAndBookingTimeBetween(
        @Param("eventId") Long eventId,
        @Param("startDate") java.time.LocalDateTime startDate,
        @Param("endDate") java.time.LocalDateTime endDate
    );
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.bookingTime >= :dateStart AND b.bookingTime < :dateEnd")
    long countByDate(
        @Param("dateStart") java.time.LocalDateTime dateStart, 
        @Param("dateEnd") java.time.LocalDateTime dateEnd
    );
    
    @Query("SELECT COUNT(DISTINCT b.user.userId) FROM Booking b " +
           "WHERE b.bookingTime >= :startDate AND b.bookingTime < :endDate")
    long countDistinctUsersByBookingTimeBetween(
        @Param("startDate") java.time.LocalDateTime startDate,
        @Param("endDate") java.time.LocalDateTime endDate
    );
}



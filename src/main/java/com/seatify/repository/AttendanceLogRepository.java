package com.seatify.repository;

import com.seatify.model.AttendanceLog;
import com.seatify.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, Long> {
    List<AttendanceLog> findByBooking(Booking booking);
}



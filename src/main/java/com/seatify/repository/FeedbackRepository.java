package com.seatify.repository;

import com.seatify.model.Event;
import com.seatify.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author : Lê Văn Nguyễn - CE181235
 */
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByEvent(Event event);
}



package com.seatify.repository;

import com.seatify.model.Event;
import com.seatify.model.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findByEvent(Event event);
}



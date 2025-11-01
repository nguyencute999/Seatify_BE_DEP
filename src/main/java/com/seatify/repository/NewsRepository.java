package com.seatify.repository;

import com.seatify.model.Event;
import com.seatify.model.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author : Lê Văn Nguyễn - CE181235
 */
public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findByEvent(Event event);
    List<News> findByIsPublishedTrue();
    List<News> findByIsPublishedTrueOrderByPublishedAtDesc();
}



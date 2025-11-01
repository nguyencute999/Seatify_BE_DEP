package com.seatify.repository;

import com.seatify.model.Event;
import com.seatify.model.EventComment;
import com.seatify.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho EventComment
 */
public interface EventCommentRepository extends JpaRepository<EventComment, Long> {
    
    /**
     * Lấy tất cả comment của một event, sắp xếp theo thời gian tạo mới nhất
     */
    @Query("SELECT c FROM EventComment c WHERE c.event.eventId = :eventId ORDER BY c.createdAt DESC")
    List<EventComment> findByEventIdOrderByCreatedAtDesc(@Param("eventId") Long eventId);
    
    /**
     * Lấy tất cả comment của một user
     */
    @Query("SELECT c FROM EventComment c WHERE c.user.userId = :userId ORDER BY c.createdAt DESC")
    List<EventComment> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    /**
     * Kiểm tra user đã comment event chưa
     */
    boolean existsByUserAndEvent(User user, Event event);
    
    /**
     * Lấy comment của user cho event cụ thể
     */
    Optional<EventComment> findByUserAndEvent(User user, Event event);
}

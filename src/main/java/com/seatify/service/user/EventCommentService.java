package com.seatify.service.user;

import com.seatify.dto.user.request.EventCommentRequest;
import com.seatify.dto.user.response.EventCommentResponse;
import com.seatify.exception.ResourceNotFoundException;
import com.seatify.exception.ValidationException;
import com.seatify.model.Booking;
import com.seatify.model.Event;
import com.seatify.model.EventComment;
import com.seatify.model.User;
import com.seatify.model.constants.BookingStatus;
import com.seatify.repository.BookingRepository;
import com.seatify.repository.EventCommentRepository;
import com.seatify.repository.EventRepository;
import com.seatify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service xử lý bình luận sự kiện
 * Yêu cầu: User phải đăng nhập và đã check-in sự kiện mới được bình luận
 * 
 * @author : Lê Văn Nguyễn - CE181235
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventCommentService {

    private final EventCommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    /**
     * Tạo bình luận mới cho sự kiện
     * 
     * Điều kiện:
     * 1. User phải đăng nhập (có userId)
     * 2. User phải có booking cho event này
     * 3. User phải đã check-in (checkInTime != null)
     * 
     * @param request Thông tin bình luận
     * @param userId ID của user đăng nhập
     * @return EventCommentResponse
     */
    @Transactional
    public EventCommentResponse createComment(EventCommentRequest request, Long userId) {
        // Validate eventId có trong request (bắt buộc khi tạo)
        if (request.getEventId() == null) {
            throw new ValidationException("Event ID is required");
        }

        // Validate user tồn tại
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate event tồn tại
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        // Validate: User phải có booking cho event này
        Booking booking = bookingRepository.findByUserAndEvent(user, event)
                .orElseThrow(() -> new ValidationException(
                    "Bạn phải đặt chỗ sự kiện này trước khi bình luận"));

        // Validate: Booking phải ở trạng thái BOOKED
        if (booking.getStatus() != BookingStatus.BOOKED) {
            throw new ValidationException("Booking không hợp lệ để bình luận");
        }

        // Validate: User phải đã check-in (checkInTime != null)
        if (booking.getCheckInTime() == null) {
            throw new ValidationException(
                "Bạn phải check-in sự kiện trước khi bình luận. Vui lòng check-in tại sự kiện.");
        }

        // Kiểm tra xem user đã comment event này chưa (tùy chọn - có thể cho phép nhiều comment)
        // Nếu muốn giới hạn 1 comment/user/event, uncomment dòng dưới:
        // if (commentRepository.existsByUserAndEvent(user, event)) {
        //     throw new ValidationException("Bạn đã bình luận sự kiện này rồi");
        // }

        // Tạo comment
        EventComment comment = EventComment.builder()
                .event(event)
                .user(user)
                .content(request.getContent().trim())
                .rating(request.getRating())
                .build();

        comment = commentRepository.save(comment);

        log.info("User {} created comment {} for event {}", userId, comment.getCommentId(), event.getEventId());

        return convertToResponse(comment);
    }

    /**
     * Lấy tất cả comment của một event
     */
    public List<EventCommentResponse> getCommentsByEventId(Long eventId) {
        // Validate event tồn tại
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found");
        }

        List<EventComment> comments = commentRepository.findByEventIdOrderByCreatedAtDesc(eventId);
        return comments.stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * Lấy tất cả comment của user hiện tại
     */
    public List<EventCommentResponse> getCommentsByUserId(Long userId) {
        List<EventComment> comments = commentRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return comments.stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * Xóa comment (chỉ user tạo comment mới xóa được)
     */
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        EventComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        // Chỉ user tạo comment mới xóa được
        if (!comment.getUser().getUserId().equals(userId)) {
            throw new ValidationException("Bạn không có quyền xóa bình luận này");
        }

        commentRepository.delete(comment);
        log.info("User {} deleted comment {}", userId, commentId);
    }

    /**
     * Cập nhật comment (chỉ user tạo comment mới sửa được)
     */
    @Transactional
    public EventCommentResponse updateComment(Long commentId, EventCommentRequest request, Long userId) {
        EventComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        // Chỉ user tạo comment mới sửa được
        if (!comment.getUser().getUserId().equals(userId)) {
            throw new ValidationException("Bạn không có quyền sửa bình luận này");
        }

        // Nếu có eventId trong request, validate không thay đổi
        if (request.getEventId() != null && !comment.getEvent().getEventId().equals(request.getEventId())) {
            throw new ValidationException("Không thể thay đổi event của bình luận");
        }

        comment.setContent(request.getContent().trim());
        comment.setRating(request.getRating());
        comment = commentRepository.save(comment);

        log.info("User {} updated comment {}", userId, commentId);

        return convertToResponse(comment);
    }

    /**
     * Convert EventComment sang EventCommentResponse
     */
    private EventCommentResponse convertToResponse(EventComment comment) {
        return EventCommentResponse.builder()
                .commentId(comment.getCommentId())
                .eventId(comment.getEvent().getEventId())
                .eventName(comment.getEvent().getEventName())
                .userId(comment.getUser().getUserId())
                .userName(comment.getUser().getFullName())
                .userEmail(comment.getUser().getEmail())
                .userAvatar(comment.getUser().getAvatarUrl()) // Avatar của người bình luận
                .content(comment.getContent())
                .rating(comment.getRating()) // Đánh giá sao
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}

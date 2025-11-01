package com.seatify.controller;

import com.seatify.dto.user.request.EventCommentRequest;
import com.seatify.dto.user.response.EventCommentResponse;
import com.seatify.service.user.AuthService;
import com.seatify.service.user.EventCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller xử lý bình luận sự kiện
 * Yêu cầu: User phải đăng nhập và đã check-in sự kiện mới được bình luận
 * 
 * Author: Lê Văn Nguyễn - CE181235
 */
@RestController
@RequestMapping("/api/v1/user/comments")
@RequiredArgsConstructor
@Tag(name = "Event Comments", description = "APIs for event comments - Requires login and check-in")
@SecurityRequirement(name = "bearerAuth") // Yêu cầu JWT token
public class EventCommentController {

    private final EventCommentService commentService;
    private final AuthService authService;

    /**
     * Tạo bình luận mới cho sự kiện
     * 
     * Yêu cầu:
     * - User phải đăng nhập (JWT token)
     * - User phải có booking cho event này
     * - User phải đã check-in sự kiện
     * 
     * @param request Thông tin bình luận
     * @param authentication JWT authentication
     * @return EventCommentResponse
     */
    @Operation(
        summary = "Tạo bình luận sự kiện",
        description = "Tạo bình luận cho sự kiện. " +
                     "Yêu cầu: User phải đăng nhập, có booking và đã check-in sự kiện."
    )
    @PostMapping
    public ResponseEntity<EventCommentResponse> createComment(
            @Valid @RequestBody EventCommentRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        EventCommentResponse response = commentService.createComment(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    /**
     * Lấy tất cả comment của một event
     * PUBLIC API - Không cần đăng nhập
     */
    @Operation(summary = "Lấy danh sách bình luận của sự kiện", 
               description = "API công khai - Không cần đăng nhập để xem bình luận")
    @SecurityRequirements // Override @SecurityRequirement ở class level - không yêu cầu auth
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<EventCommentResponse>> getCommentsByEventId(
            @PathVariable Long eventId) {
        List<EventCommentResponse> comments = commentService.getCommentsByEventId(eventId);
        return ResponseEntity.ok(comments);
    }

    /**
     * Lấy tất cả comment của user hiện tại
     */
    @Operation(summary = "Lấy danh sách bình luận của user hiện tại")
    @GetMapping("/my-comments")
    public ResponseEntity<List<EventCommentResponse>> getMyComments(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<EventCommentResponse> comments = commentService.getCommentsByUserId(userId);
        return ResponseEntity.ok(comments);
    }

    /**
     * Xóa comment của mình
     */
    @Operation(summary = "Xóa bình luận của mình")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Cập nhật comment của mình
     */
    @Operation(summary = "Cập nhật bình luận của mình")
    @PutMapping("/{commentId}")
    public ResponseEntity<EventCommentResponse> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody EventCommentRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        EventCommentResponse response = commentService.updateComment(commentId, request, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy userId từ Authentication (JWT)
     */
    private Long getUserIdFromAuthentication(Authentication authentication) {
        String email = authentication.getName();
        return authService.getUserByEmail(email).getUserId();
    }
}

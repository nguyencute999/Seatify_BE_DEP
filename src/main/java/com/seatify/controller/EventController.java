package com.seatify.controller;

import com.seatify.dto.admin.response.EventResponseDTO;
import com.seatify.dto.response.PublicEventResponseDTO;
import com.seatify.dto.response.SeatResponseDTO;
import com.seatify.dto.user.response.EventCommentResponse;
import com.seatify.service.admin.AdminEventService;
import com.seatify.service.SeatService;
import com.seatify.service.user.EventCommentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.seatify.model.constants.EventStatus;
import com.seatify.repository.specification.EventSpecifications;

/**
 * Author: Lê Văn Nguyễn - CE181235
 * Controller public cho người dùng xem danh sách và chi tiết sự kiện.
 *
 * Bao gồm các chức năng:
 *  - Lấy danh sách tất cả sự kiện (có phân trang, tìm kiếm, sắp xếp)
 *  - Xem chi tiết một sự kiện cụ thể
 *  - Lấy danh sách ghế của sự kiện
 */
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final AdminEventService eventService;//Xử lý logic sự kiện
    private final SeatService seatService;//Login chỗ ngồi
    private final EventCommentService commentService;//Xử lý bình luận sự kiện

    /**
     * Lấy danh sách tất cả sự kiện có thể hiển thị cho người dùng.
     * Có tìm kiếm theo tên, phân trang, và sắp xếp.
     *
     * @param name   Tên sự kiện cần tìm (nếu có)
     * @param page   Số trang (bắt đầu từ 0)
     * @param size   Số lượng phần tử trên mỗi trang
     * @param sortBy Thuộc tính để sắp xếp (ví dụ: startTime, eventName)
     * @param desc   true = sắp xếp giảm dần, false = tăng dần
     * @return Danh sách sự kiện dạng phân trang (Page<PublicEventResponseDTO>)
     */
    @Operation(summary = "Lấy danh sách tất cả sự kiện")
    @GetMapping
    public ResponseEntity<Page<PublicEventResponseDTO>> getAllEvents(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(required = false) EventStatus status,
            @RequestParam(defaultValue = "ALL") String time,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startTime") String sortBy,
            @RequestParam(defaultValue = "false") boolean desc
    ) {
        EventSpecifications.TimeFilter timeFilter;
        try {
            timeFilter = EventSpecifications.TimeFilter.valueOf(time.toUpperCase());
        } catch (Exception e) {
            timeFilter = EventSpecifications.TimeFilter.ALL;
        }

        Page<EventResponseDTO> events = eventService.getAll(name, status, timeFilter, page, size, sortBy, desc);
        Page<PublicEventResponseDTO> publicEvents = events.map(this::convertToPublicDTO);
        return ResponseEntity.ok(publicEvents);
    }

    /**
     * Lấy chi tiết của một sự kiện cụ thể theo ID.
     *
     * @param id ID của sự kiện cần xem
     * @return Thông tin chi tiết sự kiện (PublicEventResponseDTO)
     */
    @Operation(summary = "Xem chi tiết sự kiện")
    @GetMapping("/{id}")
    public ResponseEntity<PublicEventResponseDTO> getEventById(@PathVariable Long id) {
        EventResponseDTO event = eventService.getById(id);
        return ResponseEntity.ok(convertToPublicDTO(event));
    }

    /**
     * Lấy danh sách ghế (seat) của sự kiện theo ID sự kiện.
     * Dùng để hiển thị sơ đồ ghế hoặc chọn ghế khi người dùng đặt chỗ.
     *
     * @param id ID của sự kiện cần lấy ghế
     * @return Danh sách ghế (SeatResponseDTO)
     */
    @Operation(summary = "Lấy danh sách ghế của sự kiện", 
               description = "Lấy danh sách ghế của sự kiện để hiển thị cho người dùng")
    @GetMapping("/{id}/seats")
    public ResponseEntity<List<SeatResponseDTO>> getEventSeats(@PathVariable Long id) {
        List<SeatResponseDTO> seats = seatService.getSeatsByEventId(id);
        return ResponseEntity.ok(seats);
    }

    /**
     * Lấy danh sách sự kiện nổi bật dựa trên số lượt booking cao nhất
     * Realtime: Frontend có thể polling endpoint này mỗi 10-30 giây để cập nhật realtime
     * 
     * Logic:
     * - Chỉ lấy sự kiện UPCOMING (chưa diễn ra)
     * - Sắp xếp theo số lượng booking giảm dần
     * - Realtime: Mỗi khi có booking mới, ranking sẽ tự động thay đổi
     *
     * @param limit Số lượng sự kiện nổi bật cần lấy (mặc định 5)
     * @return Danh sách sự kiện nổi bật
     */
    @Operation(summary = "Lấy danh sách sự kiện nổi bật (dựa trên lượt booking)", 
               description = "Lấy danh sách sự kiện nổi bật dựa trên số lượt booking cao nhất. " +
                           "Realtime: Polling endpoint này để cập nhật realtime")
    @GetMapping("/featured")
    public ResponseEntity<List<PublicEventResponseDTO>> getFeaturedEvents(
            @RequestParam(defaultValue = "5") int limit
    ) {
        List<EventResponseDTO> events = eventService.getFeaturedEvents(limit);
        List<PublicEventResponseDTO> publicEvents = events.stream()
                .map(this::convertToPublicDTO)
                .toList();
        return ResponseEntity.ok(publicEvents);
    }

    /**
     * Lấy danh sách bình luận của sự kiện (Public API - không cần đăng nhập)
     */
    @Operation(summary = "Lấy danh sách bình luận của sự kiện", 
               description = "Lấy tất cả bình luận của sự kiện, sắp xếp theo thời gian mới nhất")
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<EventCommentResponse>> getEventComments(@PathVariable Long id) {
        List<EventCommentResponse> comments = commentService.getCommentsByEventId(id);
        return ResponseEntity.ok(comments);
    }

    /**
     * Chuyển đổi đối tượng EventResponseDTO (từ Admin layer)
     * sang PublicEventResponseDTO (dành cho user hiển thị).
     *
     * @param event Dữ liệu sự kiện gốc
     * @return Dữ liệu sự kiện dạng public
     */
    private PublicEventResponseDTO convertToPublicDTO(EventResponseDTO event) {
        return PublicEventResponseDTO.builder()
                .eventId(event.getEventId())
                .eventName(event.getEventName())
                .description(event.getDescription())
                .location(event.getLocation())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .capacity(event.getCapacity())
                .status(event.getStatus())
                .thumbnail(event.getThumbnail())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}

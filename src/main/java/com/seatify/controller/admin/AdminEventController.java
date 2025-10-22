package com.seatify.controller.admin;

import com.seatify.dto.admin.request.EventRequestDTO;
import com.seatify.dto.admin.response.EventResponseDTO;
import com.seatify.service.admin.AdminEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/events")
@RequiredArgsConstructor
public class AdminEventController {

    private final AdminEventService eventService;

    /**
     * Lấy sự kiện theo id
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getById(id));
    }

    /**
     * Tạo sự kiện
     */
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventResponseDTO> create(@Valid @RequestBody EventRequestDTO dto) {
        return ResponseEntity.ok(eventService.create(dto));
    }

    /**
     * cập nhật sự kiện
     */
    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventResponseDTO> update(@PathVariable Long id, @Valid @RequestBody EventRequestDTO dto) {
        return ResponseEntity.ok(eventService.update(id, dto));
    }

    /**
     * xóa sự kện theo id
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.ok("Xóa sự kiện thành công.");
    }

    /**
     * API lấy danh sách sự kiện có hỗ trợ tìm kiếm theo tên, phân trang, và sắp xếp
     * @param name Từ khóa tìm kiếm theo tên
     * @param page Số trang (bắt đầu từ 0)
     * @param size Số lượng phần tử mỗi trang
     * @param sortBy Thuộc tính để sắp xếp
     * @param desc Có sắp xếp giảm dần
     */
    @GetMapping
    public ResponseEntity<Page<EventResponseDTO>> getAllEvents(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9999") int size,
            @RequestParam(defaultValue = "eventId") String sortBy,
            @RequestParam(defaultValue = "false") boolean desc
    ) {
        return ResponseEntity.ok(eventService.getAll(name, page, size, sortBy, desc));
    }
}

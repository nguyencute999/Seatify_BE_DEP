package com.seatify.controller.admin;

import com.seatify.dto.admin.request.EventRequestDTO;
import com.seatify.dto.admin.response.EventResponseDTO;
import com.seatify.service.admin.AdminEventService;
import com.seatify.util.FileUploadUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Author: Lê Văn Nguyễn - CE181235
 * Description: Controller admin quản lý sự kiện
 * API CRUD và upload ảnh.
 */
@RestController
@RequestMapping("/api/v1/admin/events")
@RequiredArgsConstructor
public class AdminEventController {

    private final AdminEventService eventService;//Xử lý nghiệp vụ cho sự kiện.
    private final FileUploadUtil fileUploadUtil;// Hỗ trợ up file.

    /**
     * Lấy thông tin sự kiện theo ID
     *
     * @param id ID của sự kiện cần lấy
     * @return Thông tin sự kiện dạng EventResponseDTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getById(id));
    }

    /**
     * Tạo mới một sự kiện
     *
     * @param dto Dữ liệu đầu vào của sự kiện (EventRequestDTO)
     * @return Thông tin sự kiện sau khi tạo thành công
     */
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventResponseDTO> create(@Valid @RequestBody EventRequestDTO dto) {
        return ResponseEntity.ok(eventService.create(dto));
    }

    /**
     * Cập nhật thông tin một sự kiện theo ID
     *
     * @param id  ID của sự kiện cần cập nhật
     * @param dto Dữ liệu cập nhật của sự kiện
     * @return Thông tin sự kiện sau khi cập nhật
     */
    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventResponseDTO> update(@PathVariable Long id, @Valid @RequestBody EventRequestDTO dto) {
        return ResponseEntity.ok(eventService.update(id, dto));
    }

    /**
     * Xóa sự kiện theo ID
     *
     * @param id ID của sự kiện cần xóa
     * @return Thông báo xóa thành công
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.ok("Xóa sự kiện thành công.");
    }

    /**
     * Lấy danh sách tất cả sự kiện (có hỗ trợ tìm kiếm, phân trang, sắp xếp)
     *
     * @param name  Tên sự kiện cần tìm (mặc định rỗng nghĩa là lấy tất cả)
     * @param page  Trang hiện tại (mặc định = 0)
     * @param size  Số lượng phần tử mỗi trang (mặc định = 9999 để lấy tất cả)
     * @param sortBy Thuộc tính dùng để sắp xếp (mặc định = eventId)
     * @param desc  Có sắp xếp giảm dần hay không (mặc định = false)
     * @return Trang dữ liệu (Page) chứa danh sách sự kiện
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

    /**
     * Upload ảnh thumbnail cho sự kiện
     *
     * @param file File ảnh cần upload
     * @return URL của ảnh sau khi upload thành công hoặc thông báo lỗi
     */
    @PostMapping(value = "/upload-thumbnail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadThumbnail(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = fileUploadUtil.uploadFile(file, "event-thumbnails");
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to upload image: " + e.getMessage());
        }
    }
}

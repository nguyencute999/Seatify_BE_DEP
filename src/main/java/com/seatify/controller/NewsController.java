package com.seatify.controller;

import com.seatify.dto.response.PublicNewsResponseDTO;
import com.seatify.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller public cho người dùng xem tin tức đã được publish
 * 
 * @author : Lê Văn Nguyễn - CE181235
 */
@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    /**
     * Lấy tất cả tin tức đã được publish (hiển thị trên trang tin tức)
     * Sắp xếp theo thời gian publish giảm dần (mới nhất trước)
     */
    @Operation(summary = "Lấy tất cả tin tức đã publish")
    @GetMapping
    public ResponseEntity<List<PublicNewsResponseDTO>> getAllPublishedNews() {
        List<PublicNewsResponseDTO> news = newsService.getAllPublishedNews();
        return ResponseEntity.ok(news);
    }

    /**
     * Lấy chi tiết một tin tức đã được publish theo ID
     */
    @Operation(summary = "Xem chi tiết tin tức đã publish")
    @GetMapping("/{id}")
    public ResponseEntity<PublicNewsResponseDTO> getPublishedNewsById(@PathVariable Long id) {
        PublicNewsResponseDTO news = newsService.getPublishedNewsById(id);
        return ResponseEntity.ok(news);
    }
}

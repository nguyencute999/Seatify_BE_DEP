package com.seatify.service.admin;

import com.seatify.dto.admin.request.AdminNewsRequestDTO;
import com.seatify.dto.admin.response.AdminNewsResponseDTO;
import com.seatify.exception.ResourceNotFoundException;
import com.seatify.model.Event;
import com.seatify.model.News;
import com.seatify.model.User;
import com.seatify.repository.EventRepository;
import com.seatify.repository.NewsRepository;
import com.seatify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminNewsServiceImpl implements AdminNewsService {

    private final NewsRepository newsRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public AdminNewsResponseDTO getById(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News not found with id: " + id));
        return toResponse(news);
    }

    @Override
    public Page<AdminNewsResponseDTO> getAll(String title, int page, int size, String sortBy, boolean desc) {
        Sort sort = Sort.by(desc ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<News> pageData = newsRepository.findAll(pageable);
        return pageData.map(this::toResponse);
    }

    @Override
    public List<AdminNewsResponseDTO> getAllNews() {
        List<News> allNews = newsRepository.findAll();
        return allNews.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AdminNewsResponseDTO create(AdminNewsRequestDTO dto) {
        Event event = null;
        if (dto.getEventId() != null) {
            event = eventRepository.findById(dto.getEventId())
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + dto.getEventId()));
        }

        News news = News.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .thumbnail(dto.getThumbnail())
                .event(event)
                .build();

        News saved = newsRepository.save(news);
        return toResponse(saved);
    }

    @Override
    public AdminNewsResponseDTO update(Long id, AdminNewsRequestDTO dto) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News not found with id: " + id));

        Event event = null;
        if (dto.getEventId() != null) {
            event = eventRepository.findById(dto.getEventId())
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + dto.getEventId()));
        }

        news.setTitle(dto.getTitle());
        news.setContent(dto.getContent());
        news.setThumbnail(dto.getThumbnail());
        news.setEvent(event);

        News updated = newsRepository.save(news);
        return toResponse(updated);
    }

    @Override
    public AdminNewsResponseDTO publish(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News not found with id: " + id));
        news.setIsPublished(true);
        news.setPublishedAt(java.time.LocalDateTime.now()); // Cập nhật thời gian publish
        News updated = newsRepository.save(news);
        return toResponse(updated);
    }

    @Override
    public AdminNewsResponseDTO unpublish(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News not found with id: " + id));
        news.setIsPublished(false);
        News updated = newsRepository.save(news);
        return toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News not found with id: " + id));
        newsRepository.delete(news);
    }

    private AdminNewsResponseDTO toResponse(News news) {
        return AdminNewsResponseDTO.builder()
                .newsId(news.getNewsId())
                .eventId(news.getEvent() != null ? news.getEvent().getEventId() : null)
                .eventName(news.getEvent() != null ? news.getEvent().getEventName() : null) // Tên sự kiện
                .title(news.getTitle())
                .content(news.getContent())
                .thumbnail(news.getThumbnail())
                .authorEmail(news.getAuthor() != null ? news.getAuthor().getEmail() : null)
                .publishedAt(news.getPublishedAt()) // Thời gian công bố
                .createdAt(news.getPublishedAt()) // Thời gian tạo (dùng publishedAt vì được set khi tạo)
                .isPublished(news.getIsPublished()) // Trạng thái publish
                .build();
    }
}



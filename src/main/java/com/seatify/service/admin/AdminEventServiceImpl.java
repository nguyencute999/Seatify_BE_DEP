package com.seatify.service.admin;

import com.seatify.dto.admin.request.EventRequestDTO;
import com.seatify.dto.admin.response.EventResponseDTO;
import com.seatify.exception.ResourceNotFoundException;
import com.seatify.model.Event;
import com.seatify.model.User;
import com.seatify.model.constants.EventStatus;
import com.seatify.repository.EventRepository;
import com.seatify.repository.UserRepository;
import com.seatify.repository.specification.EventSpecifications;
import com.seatify.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author : Lê Văn Nguyễn - CE181235
 */
@Service
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final SeatService seatService;

    @Override
    public EventResponseDTO getById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        return convertToResponseDTO(event);
    }

    @Override
    @Transactional
    public EventResponseDTO create(EventRequestDTO dto) {
        try {
            // Validate input data using DTO validation method
            if (!dto.isValid()) {
                throw new IllegalArgumentException("Dữ liệu sự kiện không hợp lệ");
            }

            Event event = Event.builder()
                    .eventName(dto.getEventName())
                    .description(dto.getDescription())
                    .location(dto.getLocation())
                    .startTime(dto.getStartTime())
                    .endTime(dto.getEndTime())
                    .capacity(dto.getCapacity())
                    .status(dto.getStatus() != null ? dto.getStatus() : com.seatify.model.constants.EventStatus.UPCOMING)
                    .build();

            // Handle thumbnail
            if (dto.getThumbnail() != null && !dto.getThumbnail().isEmpty()) {
                event.setThumbnail(dto.getThumbnail());
            }

            // Set created by - for now we'll leave it null as it's not required in the model
            // In a real application, you would get the current user from security context
            // User currentUser = getCurrentUser(); // Implement this method
            // event.setCreatedBy(currentUser);

            Event savedEvent = eventRepository.save(event);
            
            // Tự động sinh ghế cho sự kiện
            if (dto.getSeatRows() != null && dto.getSeatsPerRow() != null) {
                seatService.generateSeatsForEvent(savedEvent, dto.getSeatRows(), dto.getSeatsPerRow());
            }
            
            return convertToResponseDTO(savedEvent);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo sự kiện: " + e.getMessage(), e);
        }
    }

    @Override
    public EventResponseDTO update(Long id, EventRequestDTO dto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

        event.setEventName(dto.getEventName());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setStartTime(dto.getStartTime());
        event.setEndTime(dto.getEndTime());
        event.setCapacity(dto.getCapacity());
        if (dto.getStatus() != null) {
            event.setStatus(dto.getStatus());
        }

        // Handle thumbnail
        if (dto.getThumbnail() != null && !dto.getThumbnail().isEmpty()) {
            event.setThumbnail(dto.getThumbnail());
        }

        Event updatedEvent = eventRepository.save(event);
        return convertToResponseDTO(updatedEvent);
    }

    @Override
    public void delete(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        eventRepository.delete(event);
    }

    @Override
    public Page<EventResponseDTO> getAll(String name, int page, int size, String sortBy, boolean desc) {
        return getAll(name, null, EventSpecifications.TimeFilter.ALL, page, size, sortBy, desc);
    }

    @Override
    public Page<EventResponseDTO> getAll(String name, EventStatus status,
                                         EventSpecifications.TimeFilter timeFilter,
                                         int page, int size, String sortBy, boolean desc) {
        Sort sort = Sort.by(desc ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Event> spec = Specification.where(EventSpecifications.nameContains(name))
                .and(EventSpecifications.hasStatus(status))
                .and(EventSpecifications.timeFilter(timeFilter));

        Page<Event> events = eventRepository.findAll(spec, pageable);

        return events.map(this::convertToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDTO> getFeaturedEvents(int limit) {
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, limit);
        
        List<Event> featuredEvents = eventRepository.findFeaturedEventsByBookingCount(
            EventStatus.UPCOMING, 
            now, 
            pageable
        );
        
        return featuredEvents.stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    private EventResponseDTO convertToResponseDTO(Event event) {
        return EventResponseDTO.builder()
                .eventId(event.getEventId())
                .eventName(event.getEventName())
                .description(event.getDescription())
                .location(event.getLocation())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .capacity(event.getCapacity())
                .status(event.getStatus())
                .thumbnail(event.getThumbnail())
                .createdBy(event.getCreatedBy() != null ? event.getCreatedBy().getEmail() : null)
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }

}

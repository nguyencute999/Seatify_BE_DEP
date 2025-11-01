package com.seatify.service.admin;

import com.seatify.dto.admin.request.EventRequestDTO;
import com.seatify.dto.admin.response.EventResponseDTO;
import com.seatify.model.constants.EventStatus;
import com.seatify.repository.specification.EventSpecifications;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author : Lê Văn Nguyễn - CE181235
 */
public interface AdminEventService {
    EventResponseDTO getById(Long id);
    EventResponseDTO create(EventRequestDTO dto);
    EventResponseDTO update(Long id, EventRequestDTO dto);
    void delete(Long id);
    Page<EventResponseDTO> getAll(String name, int page, int size, String sortBy, boolean desc);
    Page<EventResponseDTO> getAll(String name, EventStatus status,
                                  EventSpecifications.TimeFilter timeFilter,
                                  int page, int size, String sortBy, boolean desc);
    List<EventResponseDTO> getFeaturedEvents(int limit);
}

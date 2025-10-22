package com.seatify.service.admin;

import com.seatify.dto.admin.request.EventRequestDTO;
import com.seatify.dto.admin.response.EventResponseDTO;
import org.springframework.data.domain.Page;

public interface AdminEventService {
    EventResponseDTO getById(Long id);
    EventResponseDTO create(EventRequestDTO dto);
    EventResponseDTO update(Long id, EventRequestDTO dto);
    void delete(Long id);
    Page<EventResponseDTO> getAll(String name, int page, int size, String sortBy, boolean desc);
}

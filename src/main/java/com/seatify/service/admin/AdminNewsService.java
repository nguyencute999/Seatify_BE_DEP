package com.seatify.service.admin;

import com.seatify.dto.admin.request.AdminNewsRequestDTO;
import com.seatify.dto.admin.response.AdminNewsResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AdminNewsService {
    AdminNewsResponseDTO getById(Long id);
    Page<AdminNewsResponseDTO> getAll(String title, int page, int size, String sortBy, boolean desc);
    List<AdminNewsResponseDTO> getAllNews();
    AdminNewsResponseDTO create(AdminNewsRequestDTO dto);
    AdminNewsResponseDTO update(Long id, AdminNewsRequestDTO dto);
    AdminNewsResponseDTO publish(Long id); // Publish tin tức
    AdminNewsResponseDTO unpublish(Long id); // Unpublish tin tức
    void delete(Long id);
}



package com.seatify.service;

import com.seatify.dto.response.PublicNewsResponseDTO;

import java.util.List;

public interface NewsService {
    List<PublicNewsResponseDTO> getAllPublishedNews();
    PublicNewsResponseDTO getPublishedNewsById(Long id);
}

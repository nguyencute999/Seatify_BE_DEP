package com.seatify.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record OAuth2CodeRequestDTO(
    @NotBlank(message = "Code không được để trống")
    String code, 
    
    @NotBlank(message = "Redirect URI không được để trống")
    String redirectUri
) { }



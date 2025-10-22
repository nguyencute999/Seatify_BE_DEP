package com.seatify.dto.auth;

public record UserInfoResponse(Long userId, String fullName, String email, String phone, String avatarUrl, String provider) { }



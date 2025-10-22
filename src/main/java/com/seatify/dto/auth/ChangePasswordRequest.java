package com.seatify.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
    @NotBlank(message = "Mật khẩu cũ không được để trống")
    String oldPassword, 
    
    @NotBlank(message = "Mật khẩu mới không được để trống")
    String newPassword, 
    
    @NotBlank(message = "Xác nhận mật khẩu mới không được để trống")
    String confirmNewPassword
) { }



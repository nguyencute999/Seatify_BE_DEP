package com.seatify.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordWithOtpRequest(
        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email phải có @")
        String email,
        
        @NotBlank(message = "OTP không được để trống")
        @Pattern(regexp = "^[0-9]{6}$", message = "OTP phải có đúng 6 số")
        String otp,
        
        @NotBlank(message = "Mật khẩu mới không được để trống")
        String newPassword,
        
        @NotBlank(message = "Xác nhận mật khẩu không được để trống")
        String confirmPassword
) { }



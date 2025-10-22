package com.seatify.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record FormLogin(
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email phải có @")
    String email, 
    
    @NotBlank(message = "Mật khẩu không được để trống")
    String password
) { }



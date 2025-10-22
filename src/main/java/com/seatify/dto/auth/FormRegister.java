package com.seatify.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record FormRegister(
    @NotBlank(message = "MSSV không được để trống")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "MSSV phải có chữ và số")
    String mssv, 
    
    @NotBlank(message = "Họ tên không được để trống")
    String fullName, 
    
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email phải có @")
    String email, 
    
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10}$", message = "Số điện thoại phải có đúng 10 số")
    String phone, 
    
    @NotBlank(message = "Mật khẩu không được để trống")
    String password,
    
    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    String confirmPassword
) { }



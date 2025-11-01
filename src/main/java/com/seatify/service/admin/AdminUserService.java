package com.seatify.service.admin;

import com.seatify.dto.admin.request.AdminUserRequestDTO;
import com.seatify.dto.admin.response.AdminUserResponseDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface AdminUserService {
    List<AdminUserResponseDTO> getAllUsers();
    AdminUserResponseDTO addUser(AdminUserRequestDTO dto);
    AdminUserResponseDTO addUserMultipart(
        String fullName,
        String email,
        String mssv,
        String phone,
        String password,
        String confirmPassword,
        List<String> roles,
        MultipartFile avatar
    );
}

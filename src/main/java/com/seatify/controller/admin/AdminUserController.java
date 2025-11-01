package com.seatify.controller.admin;

import com.seatify.dto.admin.response.AdminUserResponseDTO;
import com.seatify.service.admin.AdminUserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Author: Lê Văn Nguyễn - CE181235
 * @description Controller admin quản lý user
 * lấy tất cả danh sách all user và create,...
 */
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final AdminUserService adminUserService;//xử lý logic user

    /**
     * Lớp bọc (wrapper class) dùng để trả về giá trị chung theo dạng JSON { "value": ... }.
     * @param <T> Kiểu dữ liệu trả về (generic)
     */
    public static class ValueResponse<T> {
        private T value;
        public ValueResponse(T value) { this.value = value; }
        public T getValue() { return value; }
        public void setValue(T value) { this.value = value; }
    }

    /**
     * api lấy toàn bộ danh sách người dùng
     * @return Danh sách người dùng (List<AdminUserResponseDTO>)
     */
    @GetMapping
    public ResponseEntity<List<AdminUserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(adminUserService.getAllUsers());
    }


    /**
     * api tạo mới người dùng (hỗ trợ upload avatar).
     * Sử dụng multipart/form-data để gửi kèm file hình ảnh.
     *
     * @param fullName Họ và tên người dùng (bắt buộc)
     * @param email Địa chỉ email hợp lệ (bắt buộc)
     * @param mssv Mã số sinh viên (tuỳ chọn)
     * @param phone Số điện thoại (tuỳ chọn)
     * @param password Mật khẩu đăng nhập (bắt buộc)
     * @param confirmPassword Xác nhận mật khẩu (bắt buộc)
     * @param roles Danh sách vai trò (bắt buộc)
     * @param avatar Ảnh đại diện (tuỳ chọn, kiểu file)
     * @return Thông tin người dùng vừa tạo, gói trong ValueResponse
     */
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ValueResponse<AdminUserResponseDTO>> addUser(
         @RequestParam @NotBlank String fullName,
         @RequestParam @NotBlank @Email String email,
         @RequestParam(required = false) String mssv,
         @RequestParam(required = false) String phone,
         @RequestParam @NotBlank String password,
         @RequestParam @NotBlank String confirmPassword,
         @RequestParam List<String> roles,
         @RequestParam(required = false) MultipartFile avatar
    ) {
        AdminUserResponseDTO createdUser = adminUserService.addUserMultipart(
            fullName, email, mssv, phone, password, confirmPassword, roles, avatar
        );
        return ResponseEntity.ok(new ValueResponse<>(createdUser));
    }
}

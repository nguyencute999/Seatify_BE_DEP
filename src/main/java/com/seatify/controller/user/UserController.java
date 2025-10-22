package com.seatify.controller.user;

import com.seatify.dto.auth.ChangePasswordRequest;
import com.seatify.dto.auth.ResponseWrapper;
import com.seatify.dto.auth.UpdateUserRequest;
import com.seatify.dto.auth.UserInfoResponse;
import com.seatify.service.user.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    @Operation(summary = "Lấy thông tin người dùng hiện tại")
    @GetMapping("/profile")
    public ResponseEntity<ResponseWrapper<UserInfoResponse>> getMe(Authentication authentication) {
        String email = authentication.getName();
        UserInfoResponse info = authService.getUserInfo(email);
        return ResponseEntity.ok(
                ResponseWrapper.<UserInfoResponse>builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .message("Lấy thông tin người dùng thành công")
                        .data(info)
                        .build()
        );
    }

    @Operation(summary = "Cập nhật thông tin người dùng")
    @PutMapping("/update")
    public ResponseEntity<ResponseWrapper<String>> update(Authentication authentication,
                                                         @Valid @RequestBody UpdateUserRequest request) {
        String email = authentication.getName();
        authService.updateUserInfo(email, request);
        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .message("Cập nhật thông tin thành công")
                        .data("OK")
                        .build()
        );
    }

    @Operation(summary = "Đổi mật khẩu")
    @PutMapping("/change-password")
    public ResponseEntity<ResponseWrapper<String>> changePassword(Authentication authentication,
                                                                 @Valid @RequestBody ChangePasswordRequest request) {
        String email = authentication.getName();
        authService.changePassword(email, request);
        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .message("Đổi mật khẩu thành công")
                        .data("OK")
                        .build()
        );
    }
}



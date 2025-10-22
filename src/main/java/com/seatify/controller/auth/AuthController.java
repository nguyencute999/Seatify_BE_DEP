package com.seatify.controller.auth;

import com.seatify.dto.auth.*;
import com.seatify.service.user.AuthService;
import com.seatify.service.user.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @Operation(summary = "Đăng nhập tài khoản")
    @PostMapping("/login")
    public ResponseEntity<?> handleLogin(@Valid @RequestBody FormLogin formLogin) {
        var loginResponse = authService.login(formLogin);
        return ResponseEntity.ok(loginResponse);
    }

    @Operation(summary = "Đăng ký tài khoản")
    @PostMapping("/register")
    public ResponseEntity<?> handleRegister(@Valid @RequestBody FormRegister formRegister) {
        authService.register(formRegister);
        return ResponseEntity.created(URI.create("api/v1/auth/register")).body(
                ResponseWrapper.builder()
                        .status(HttpStatus.CREATED).code(201)
                        .data("Đăng ký tài khoản thành công").build());
    }

    @Operation(summary = "Đăng xuất tài khoản")
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok("Đăng xuất thành công.");
    }

//    @Operation(summary = "Quên mật khẩu")
//    @PostMapping("/forgot-password")
//    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
//        passwordResetService.processForgotPassword(request);
//        return ResponseEntity.ok("Vui lòng kiểm tra email để đặt lại mật khẩu.");
//    }

    @Operation(summary = "Quên mật khẩu - gửi OTP qua email")
    @PostMapping("/forgot-password/otp")
    public ResponseEntity<?> forgotPasswordOtp(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.processForgotPasswordWithOtp(request);
        return ResponseEntity.ok("Đã gửi OTP tới email. Vui lòng kiểm tra hộp thư.");
    }

//    @Operation(summary = "Đặt lại mật khẩu")
//    @PostMapping("/reset-password")
//    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
//        passwordResetService.resetPassword(request);
//        return ResponseEntity.ok("Mật khẩu đã được cập nhật thành công.");
//    }

    @Operation(summary = "Đặt lại mật khẩu bằng OTP")
    @PostMapping("/reset-password/otp")
    public ResponseEntity<?> resetPasswordWithOtp(@Valid @RequestBody ResetPasswordWithOtpRequest request) {
        passwordResetService.resetPasswordWithOtp(request);
        return ResponseEntity.ok("Mật khẩu đã được cập nhật thành công.");
    }


    @GetMapping("/google-login")
    public ResponseEntity<?> redirectToGoogle(HttpServletRequest request) {
        String redirectUrl = authService.getGoogleRedirectUrl(request);
        return ResponseEntity.ok(ResponseWrapper.builder()
                .status(HttpStatus.OK).code(200)
                .message("API login Google")
                .data(new AuthRedirectResponse(redirectUrl)).build());
    }

    @Operation(summary = "Nhận mã code từ Google và trả về JWT")
    @PostMapping("/google/code")
    public ResponseEntity<?> handleGoogleCode(@Valid @RequestBody OAuth2CodeRequestDTO request) {
        try {
            var response = authService.exchangeGoogleCodeForToken(request.code(), request.redirectUri());
            return ResponseEntity.ok(ResponseWrapper.builder()
                    .status(HttpStatus.OK).code(200)
                    .message("Đăng nhập Google thành công").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseWrapper.builder()
                    .status(HttpStatus.BAD_REQUEST).code(400)
                    .message("Đăng nhập Google thất bại: " + e.getMessage()).build());
        }
    }

    @Operation(summary = "Callback endpoint cho Google OAuth2")
    @GetMapping("/oauth2/callback/google")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam("code") String code, 
                                                  @RequestParam(value = "state", required = false) String state) {
        try {
            String redirectUri = "http://localhost:3000/oauth2/redirect";
            var response = authService.exchangeGoogleCodeForToken(code, redirectUri);
            
            String frontendUrl = "http://localhost:3000";
            String redirectUrl = frontendUrl + "/oauth2/redirect?token=" + response.accessToken + "&roles=" + String.join(",", response.roles);
            
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", redirectUrl)
                    .build();
        } catch (Exception e) {
            String frontendUrl = "http://localhost:3000";
            String errorUrl = frontendUrl + "/oauth2/redirect?error=login_failed&message=" + e.getMessage();
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", errorUrl)
                    .build();
        }
    }
}



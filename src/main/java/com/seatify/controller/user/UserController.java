package com.seatify.controller.user;

import com.seatify.dto.auth.ChangePasswordRequest;
import com.seatify.dto.auth.ResponseWrapper;
import com.seatify.dto.auth.UpdateUserRequest;
import com.seatify.dto.auth.UserInfoResponse;
import com.seatify.service.user.AuthService;
import com.seatify.util.FileUploadUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;


/**
 * @author : Lê Văn Nguyễn - CE181235
 * Controller quản lý các API liên quan đến người dùng như xem và cập nhật thông tin cá nhân, đổi mật khẩu, upload ảnh đại diện, và kiểm tra kết nối Cloudinary.
 *
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;
    private final FileUploadUtil fileUploadUtil;

    /**
     * Lấy thông tin của người
     *
     * @param authentication thông tin xác thực của người dùng hiện tại
     * @return thông tin chi tiết của người dùng
     */
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

    /**
     * Cập nhật thông tin người dùng (tên, số điện thoại, địa chỉ, v.v.).
     *
     * @param authentication thông tin xác thực của người dùng
     * @param request thông tin cần cập nhật của người dùng
     * @return thông báo cập nhật thành công
     */
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

    /**
     * Đổi mật khẩu cho người dùng hiện tại.
     *
     * @param authentication thông tin xác thực của người dùng
     * @param request đối tượng chứa mật khẩu cũ và mật khẩu mới
     * @return phản hồi đổi mật khẩu thành công
     */
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

    /**
     * Upload ảnh đại diện (avatar) của người dùng lên Cloudinary.
     *
     * @param authentication thông tin xác thực của người dùng
     * @param file file hình ảnh cần upload
     * @return đường dẫn URL của ảnh sau khi upload thành công
     */
    @Operation(summary = "Upload ảnh đại diện")
    @PostMapping(value = "/upload-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseWrapper<String>> uploadAvatar(Authentication authentication,
                                                               @RequestParam("file") MultipartFile file) {
        try {
            String email = authentication.getName();
            String imageUrl = fileUploadUtil.uploadFile(file, "user-avatars");
            
            // Update user's avatar URL in database
            authService.updateUserAvatar(email, imageUrl);
            
            return ResponseEntity.ok(
                    ResponseWrapper.<String>builder()
                            .status(HttpStatus.OK)
                            .code(200)
                            .message("Upload ảnh đại diện thành công")
                            .data(imageUrl)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ResponseWrapper.<String>builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .code(400)
                            .message("Lỗi khi upload ảnh: " + e.getMessage())
                            .data(null)
                            .build()
            );
        }
    }

    /**
     * kiểm tra kết nối Cloudinary (chạy thử upload 1 file text giả định).
     *
     * @return phản hồi cho biết Cloudinary có hoạt động hay không
     */
//    @Operation(summary = "Test Cloudinary connection")
//    @GetMapping("/test-cloudinary")
//    public ResponseEntity<ResponseWrapper<String>> testCloudinary() {
//        try {
//            // Test with a simple text file
//            String testContent = "Test file for Cloudinary connection";
//            byte[] testBytes = testContent.getBytes();
//
//            org.springframework.web.multipart.MultipartFile testFile = new org.springframework.web.multipart.MultipartFile() {
//                @Override
//                public String getName() { return "test"; }
//                @Override
//                public String getOriginalFilename() { return "test.txt"; }
//                @Override
//                public String getContentType() { return "text/plain"; }
//                @Override
//                public boolean isEmpty() { return false; }
//                @Override
//                public long getSize() { return testBytes.length; }
//                @Override
//                public byte[] getBytes() throws IOException { return testBytes; }
//                @Override
//                public java.io.InputStream getInputStream() throws IOException {
//                    return new java.io.ByteArrayInputStream(testBytes);
//                }
//                @Override
//                public void transferTo(java.io.File dest) throws IOException, IllegalStateException {}
//            };
//
//            String result = fileUploadUtil.uploadFile(testFile, "test");
//            return ResponseEntity.ok(
//                    ResponseWrapper.<String>builder()
//                            .status(HttpStatus.OK)
//                            .code(200)
//                            .message("Cloudinary connection successful")
//                            .data(result)
//                            .build()
//            );
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(
//                    ResponseWrapper.<String>builder()
//                            .status(HttpStatus.BAD_REQUEST)
//                            .code(400)
//                            .message("Cloudinary connection failed: " + e.getMessage())
//                            .data(null)
//                            .build()
//            );
//        }
//    }
}



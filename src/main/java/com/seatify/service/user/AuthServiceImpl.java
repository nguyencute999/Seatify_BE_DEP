package com.seatify.service.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seatify.dto.auth.*;
import com.seatify.exception.ResourceNotFoundException;
import com.seatify.exception.ValidationException;
import com.seatify.model.RoleEntity;
import com.seatify.model.User;
import com.seatify.model.constants.AuthProvider;
import com.seatify.model.constants.Role;
import com.seatify.repository.RoleRepository;
import com.seatify.repository.UserRepository;
import com.seatify.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwt;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${spring.security.oauth2.client.registration.google.client-id:}")
    private String googleClientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret:}")
    private String googleClientSecret;
    @Value("${seatify.oauth2.google.token-uri}")
    private String googleTokenUri;
    @Value("${seatify.oauth2.google.userinfo-uri}")
    private String googleUserInfoUri;
    @Value("${seatify.oauth2.google.redirect-uri}")
    private String googleRedirectUri;

    @Override
    public LoginResponse login(FormLogin form) {
        var user = userRepo.findByEmail(form.email())
            .orElseThrow(() -> new ResourceNotFoundException("Email không tồn tại"));
        if (user.getPasswordHash() == null || !encoder.matches(form.password(), user.getPasswordHash())) {
            throw new ValidationException("Sai mật khẩu");
        }
        
        // Lấy roles từ user và tạo authorities
        Set<RoleEntity> userRoles = user.getRoles();
        if (userRoles == null || userRoles.isEmpty()) {
            throw new ValidationException("User không có role được gán");
        }
        
        Collection<String> authorities = userRoles.stream()
                .map(role -> "ROLE_" + role.getRoleName().name())
                .collect(Collectors.toList());
        
        List<String> rolesList = userRoles.stream()
                .map(role -> "ROLE_" + role.getRoleName().name())
                .collect(Collectors.toList());
        
        String token = jwt.generateToken(user.getEmail(), authorities);
        
        return LoginResponse.builder()
                .accessToken(token)
                .roles(rolesList)
                .build();
    }

    @Override
    public void register(FormRegister form) {
        if (userRepo.existsByEmail(form.email())) throw new ValidationException("Email đã tồn tại");
        if (userRepo.existsByMssv(form.mssv())) throw new ValidationException("MSSV đã tồn tại");
        if (!form.password().equals(form.confirmPassword())) throw new ValidationException("Mật khẩu xác nhận không khớp");
        
        // Lấy role USER mặc định
        RoleEntity userRole = roleRepo.findByRoleName(Role.USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role USER không tồn tại"));
        
        var user = User.builder()
                .mssv(form.mssv())
                .fullName(form.fullName())
                .email(form.email())
                .phone(form.phone())
                .passwordHash(encoder.encode(form.password()))
                .authProvider(AuthProvider.LOCAL)
                .roles(Set.of(userRole))
                .build();
        userRepo.save(user);
    }

    @Override
    public UserInfoResponse getUserInfo(String email) {
        var u = userRepo.findByEmail(email).orElseThrow();
        return new UserInfoResponse(u.getUserId(), u.getFullName(), u.getEmail(), u.getPhone(), u.getAvatarUrl(), u.getAuthProvider().name());
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }

    @Override
    public void updateUserInfo(String email, UpdateUserRequest req) {
        var u = userRepo.findByEmail(email).orElseThrow();
        if (req.fullName() != null) u.setFullName(req.fullName());
        if (req.avatarUrl() != null) u.setAvatarUrl(req.avatarUrl());
        if (req.phone() != null) u.setPhone(req.phone());
        userRepo.save(u);
    }

    @Override
    public void changePassword(String email, ChangePasswordRequest req) {
        var u = userRepo.findByEmail(email).orElseThrow();
        
        // Validate old password
        if (u.getPasswordHash() == null || !encoder.matches(req.oldPassword(), u.getPasswordHash()))
            throw new ValidationException("Mật khẩu cũ không đúng");
        
        // Validate new password and confirmation match
        if (!req.newPassword().equals(req.confirmNewPassword()))
            throw new ValidationException("Mật khẩu mới và xác nhận mật khẩu không khớp");
        
        // Validate new password is different from old password
        if (encoder.matches(req.newPassword(), u.getPasswordHash()))
            throw new ValidationException("Mật khẩu mới phải khác mật khẩu cũ");
        
        u.setPasswordHash(encoder.encode(req.newPassword()));
        userRepo.save(u);
    }

    @Override
    public void updateUserAvatar(String email, String avatarUrl) {
        var user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setAvatarUrl(avatarUrl);
        userRepo.save(user);
    }

    @Override
    public String getGoogleRedirectUrl(jakarta.servlet.http.HttpServletRequest request) {
        String scope = "openid%20email%20profile";
        String redirect = java.net.URLEncoder.encode(googleRedirectUri, java.nio.charset.StandardCharsets.UTF_8);
        return "https://accounts.google.com/o/oauth2/v2/auth"
                + "?response_type=code"
                + "&client_id=" + googleClientId
                + "&redirect_uri=" + redirect
                + "&scope=" + scope
                + "&access_type=offline";
    }

    @Override
    public OAuth2ResponseDTO exchangeGoogleCodeForToken(String code, String redirectUri) {
        try {
            // 1. Decode code nếu có ký tự encode
            String decodedCode = URLDecoder.decode(code, StandardCharsets.UTF_8);

            // 2. Gửi code tới Google để lấy access token
            String tokenUrl = "https://oauth2.googleapis.com/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("code", decodedCode);
            body.add("client_id", googleClientId);
            body.add("client_secret", googleClientSecret);
            body.add("redirect_uri", redirectUri);
            body.add("grant_type", "authorization_code");

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();

            var tokenResponseEntity = restTemplate.postForEntity(tokenUrl, requestEntity, Map.class);
            Map<String, Object> tokenResponse = tokenResponseEntity.getBody();

            if (tokenResponse == null || tokenResponse.get("access_token") == null) {
                throw new RuntimeException("Không lấy được access token từ Google");
            }

            String accessToken = (String) tokenResponse.get("access_token");

            // 3. Dùng access token gọi API lấy thông tin user
            String infoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";
            HttpHeaders userHeaders = new HttpHeaders();
            userHeaders.setBearerAuth(accessToken);
            HttpEntity<Void> userEntity = new HttpEntity<>(userHeaders);

            var userInfoResponse = restTemplate.exchange(infoUrl, HttpMethod.GET, userEntity, Map.class);
            Map<String, Object> userInfo = userInfoResponse.getBody();

            if (userInfo == null || userInfo.get("email") == null) {
                throw new RuntimeException("Không lấy được thông tin người dùng từ Google");
            }

            String email = (String) userInfo.get("email");
            String firstName = (String) userInfo.get("given_name");
            String lastName = (String) userInfo.get("family_name");
            String fullName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
            String picture = userInfo.get("picture") != null ? (String) userInfo.get("picture") : null;

            User user = userRepo.findByEmail(email).orElse(null);

            if (user == null) {
                // Lấy role USER mặc định
                RoleEntity defaultRole = roleRepo.findByRoleName(Role.USER)
                        .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));

                String rawPassword = generateRandomPassword(12);
                String encodedPassword = encoder.encode(rawPassword);

                user = User.builder()
                        .email(email)
                        .fullName(fullName.trim())
                        .phone("")
                        .passwordHash(encodedPassword)
                        .authProvider(AuthProvider.GOOGLE)
                        .roles(Set.of(defaultRole))
                        .avatarUrl(picture)
                        .build();

                userRepo.save(user);
            }

            // 4. Tạo JWT và trả về
            Set<RoleEntity> userRoles = user.getRoles();
            Collection<String> authorities = userRoles.stream()
                    .map(role -> "ROLE_" + role.getRoleName().name())
                    .collect(Collectors.toList());

            String token = jwt.generateToken(user.getEmail(), authorities);

            List<String> rolesList = userRoles.stream()
                    .map(role -> "ROLE_" + role.getRoleName().name())
                    .collect(Collectors.toList());

            return OAuth2ResponseDTO.builder()
                    .accessToken(token)
                    .roles(rolesList)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            throw new ValidationException("Lỗi khi xử lý Google login: " + e.getMessage());
        }
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }
}



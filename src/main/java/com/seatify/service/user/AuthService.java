package com.seatify.service.user;

import com.seatify.dto.auth.*;
import com.seatify.model.User;

public interface AuthService {
    LoginResponse login(FormLogin form);
    void register(FormRegister form);
    UserInfoResponse getUserInfo(String email);
    User getUserByEmail(String email);
    void updateUserInfo(String email, UpdateUserRequest req);
    void changePassword(String email, ChangePasswordRequest req);
    void updateUserAvatar(String email, String avatarUrl);

    String getGoogleRedirectUrl(jakarta.servlet.http.HttpServletRequest request);
    OAuth2ResponseDTO exchangeGoogleCodeForToken(String code, String redirectUri);
}



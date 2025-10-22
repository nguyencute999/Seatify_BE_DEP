package com.seatify.dto.auth;

import lombok.Builder;
import java.util.List;

@Builder
public class LoginResponse {
    public String accessToken;
    public List<String> roles;
}

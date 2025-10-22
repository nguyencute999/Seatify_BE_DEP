package com.seatify.service.user;

import com.seatify.dto.auth.ForgotPasswordRequest;
import com.seatify.dto.auth.ResetPasswordRequest;
import com.seatify.dto.auth.ResetPasswordWithOtpRequest;

public interface PasswordResetService {
    void processForgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
    void processForgotPasswordWithOtp(ForgotPasswordRequest request);
    void resetPasswordWithOtp(ResetPasswordWithOtpRequest request);
}



package com.seatify.repository;

import com.seatify.model.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author : Lê Văn Nguyễn - CE181235
 */
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {
    Optional<PasswordResetOtp> findTopByEmailAndUsedOrderByIdDesc(String email, Boolean used);
}

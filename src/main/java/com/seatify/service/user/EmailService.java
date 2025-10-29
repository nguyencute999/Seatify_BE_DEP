package com.seatify.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendSimpleMail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("seatify.system9@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public void sendOtpEmail(String to, String displayName, String otp) {
        String subject = "Mã OTP xác thực";
        String body = "Xin chào " + displayName + "!\n\n"
                + "Seatify gửi bạn mã OTP xác thực:\n\n"
                + " Mã OTP: " + otp + " \n"
                + "Hết hạn trong 10 phút \n\n"
                + " Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email. \n"
                + "Team Seatify";
        sendSimpleMail(to, subject, body);
    }

    public void sendBookingConfirmationEmail(String to, String subject, String htmlBody, String qrCodeFilePath) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom("seatify.system9@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true indicates HTML content
            
            // Attach QR code as image from file path
            if (qrCodeFilePath != null && !qrCodeFilePath.isEmpty()) {
                java.io.File qrCodeFile = new java.io.File(qrCodeFilePath);
                if (qrCodeFile.exists()) {
                    helper.addAttachment("qr-code.png", qrCodeFile);
                }
            }
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send booking confirmation email", e);
        }
    }
}



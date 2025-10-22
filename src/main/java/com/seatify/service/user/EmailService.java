package com.seatify.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
}



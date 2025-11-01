package com.seatify.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

/**
 * Config để set timezone mặc định cho ứng dụng
 * Set timezone là Asia/Ho_Chi_Minh (GMT+7) - Giờ Việt Nam
 * 
 * @author : Lê Văn Nguyễn - CE181235
 */
@Configuration
public class TimezoneConfig {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
    }
}

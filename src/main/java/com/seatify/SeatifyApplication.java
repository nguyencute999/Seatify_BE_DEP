package com.seatify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SeatifyApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeatifyApplication.class, args);
    }
}

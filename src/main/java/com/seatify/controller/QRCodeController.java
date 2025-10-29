package com.seatify.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Deprecated // Local QR file serving removed; use Cloudinary URLs stored with bookings
@RestController
@RequestMapping("/api/v1/qr")
public class QRCodeController {

    @GetMapping("/{filename}")
    public ResponseEntity<String> getQRCode(@PathVariable String filename) {
        return ResponseEntity.status(HttpStatus.GONE)
                .body("QR code files are no longer served locally. Use Cloudinary URL stored in booking.");
    }
}

package com.servientrega.locker.controller;

import com.google.zxing.WriterException;
import com.servientrega.locker.service.QRCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
@Tag(name = "QR Code", description = "QR code generation endpoints")
public class QRCodeController {

    private final QRCodeService qrCodeService;

    @GetMapping("/retrieval-code/{code}")
    @Operation(summary = "Generate QR code", description = "Generates a QR code image for a retrieval code")
    public ResponseEntity<byte[]> generateRetrievalCodeQR(
            @PathVariable String code,
            @RequestParam(defaultValue = "300") int width,
            @RequestParam(defaultValue = "300") int height) {
        
        try {
            byte[] qrCode = qrCodeService.generateQRCode(code, width, height);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            
            return new ResponseEntity<>(qrCode, headers, HttpStatus.OK);
        } catch (WriterException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

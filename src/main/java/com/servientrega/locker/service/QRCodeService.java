package com.servientrega.locker.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QRCodeService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public byte[] generateQRCode(String code, String pin, String trackingNumber) throws WriterException, IOException {
        Map<String, String> qrData = new HashMap<>();
        qrData.put("code", code);
        qrData.put("pin", pin);
        qrData.put("tracking", trackingNumber);
        
        String jsonContent = objectMapper.writeValueAsString(qrData);
        return generateQRCodeFromText(jsonContent, 400, 400);
    }

    public byte[] generateQRCode(String text, int width, int height) throws WriterException, IOException {
        return generateQRCodeFromText(text, width, height);
    }
    
    private byte[] generateQRCodeFromText(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        
        return outputStream.toByteArray();
    }
}

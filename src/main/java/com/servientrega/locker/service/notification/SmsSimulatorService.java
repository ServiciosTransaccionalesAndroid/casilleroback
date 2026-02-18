package com.servientrega.locker.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SmsSimulatorService {

    public void sendSms(String phoneNumber, String message) {
        log.info("╔════════════════════════════════════════════════════════════════╗");
        log.info("║                      SMS SENT                                  ║");
        log.info("╠════════════════════════════════════════════════════════════════╣");
        log.info("║ To: {}", String.format("%-58s", phoneNumber) + "║");
        log.info("║ Message: {}", String.format("%-53s", truncate(message, 53)) + "║");
        log.info("╚════════════════════════════════════════════════════════════════╝");
    }

    private String truncate(String text, int maxLength) {
        return text.length() > maxLength ? text.substring(0, maxLength - 3) + "..." : text;
    }
}

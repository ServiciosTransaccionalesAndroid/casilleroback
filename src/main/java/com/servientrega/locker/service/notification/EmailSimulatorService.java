package com.servientrega.locker.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailSimulatorService {

    public void sendEmail(String email, String subject, String body) {
        log.info("╔════════════════════════════════════════════════════════════════╗");
        log.info("║                     EMAIL SENT                                 ║");
        log.info("╠════════════════════════════════════════════════════════════════╣");
        log.info("║ To: {}", String.format("%-58s", email) + "║");
        log.info("║ Subject: {}", String.format("%-53s", truncate(subject, 53)) + "║");
        log.info("║ Body: {}", String.format("%-56s", truncate(body, 56)) + "║");
        log.info("╚════════════════════════════════════════════════════════════════╝");
    }

    private String truncate(String text, int maxLength) {
        return text.length() > maxLength ? text.substring(0, maxLength - 3) + "..." : text;
    }
}

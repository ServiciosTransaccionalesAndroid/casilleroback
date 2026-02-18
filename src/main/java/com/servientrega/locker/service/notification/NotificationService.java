package com.servientrega.locker.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SmsSimulatorService smsSimulatorService;
    private final EmailSimulatorService emailSimulatorService;
    private final TemplateService templateService;

    public void sendRetrievalCodeNotification(String recipientName, String recipientPhone, 
                                              String recipientEmail, String code, 
                                              String lockerName, String lockerAddress, 
                                              LocalDateTime expiresAt) {
        log.info("Sending retrieval code notification to {}", recipientName);

        String smsMessage = templateService.getRetrievalCodeSmsTemplate(code, lockerName, expiresAt);
        smsSimulatorService.sendSms(recipientPhone, smsMessage);

        if (recipientEmail != null && !recipientEmail.isEmpty()) {
            String emailSubject = templateService.getRetrievalCodeEmailSubject();
            String emailBody = templateService.getRetrievalCodeEmailBody(
                recipientName, code, lockerName, lockerAddress, expiresAt
            );
            emailSimulatorService.sendEmail(recipientEmail, emailSubject, emailBody);
        }

        log.info("Retrieval code notification sent successfully");
    }

    public void sendExpirationAlert(String recipientName, String recipientPhone, 
                                   String recipientEmail, String code, int hoursRemaining) {
        log.info("Sending expiration alert for code {} ({} hours remaining)", code, hoursRemaining);

        String smsMessage = templateService.getExpirationAlertSmsTemplate(code, hoursRemaining);
        smsSimulatorService.sendSms(recipientPhone, smsMessage);

        if (recipientEmail != null && !recipientEmail.isEmpty()) {
            String emailSubject = templateService.getExpirationAlertEmailSubject();
            String emailBody = templateService.getExpirationAlertEmailBody(
                recipientName, code, hoursRemaining
            );
            emailSimulatorService.sendEmail(recipientEmail, emailSubject, emailBody);
        }

        log.info("Expiration alert sent successfully");
    }

    public void sendDeliveryConfirmation(String recipientName, String recipientPhone, 
                                        String recipientEmail, String trackingNumber, 
                                        LocalDateTime deliveryTime) {
        log.info("Sending delivery confirmation for tracking {}", trackingNumber);

        String smsMessage = templateService.getDeliveryConfirmationSmsTemplate(trackingNumber);
        smsSimulatorService.sendSms(recipientPhone, smsMessage);

        if (recipientEmail != null && !recipientEmail.isEmpty()) {
            String emailSubject = templateService.getDeliveryConfirmationEmailSubject();
            String emailBody = templateService.getDeliveryConfirmationEmailBody(
                recipientName, trackingNumber, deliveryTime
            );
            emailSimulatorService.sendEmail(recipientEmail, emailSubject, emailBody);
        }

        log.info("Delivery confirmation sent successfully");
    }

    public void sendMaintenanceAlert(String technicianEmail, String lockerName, 
                                    String compartmentNumber, String issue) {
        log.info("Sending maintenance alert for locker {} - compartment {}", lockerName, compartmentNumber);

        String emailSubject = templateService.getMaintenanceAlertEmailSubject(lockerName);
        String emailBody = templateService.getMaintenanceAlertEmailBody(
            lockerName, compartmentNumber, issue
        );
        emailSimulatorService.sendEmail(technicianEmail, emailSubject, emailBody);

        log.info("Maintenance alert sent successfully");
    }
}

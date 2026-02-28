package com.servientrega.locker.service;

import com.servientrega.locker.entity.RetrievalCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final QRCodeService qrCodeService;

    @Async
    public void sendRetrievalCodeEmail(RetrievalCode retrievalCode, String recipientEmail, 
                                       String recipientName, String trackingNumber,
                                       String lockerName, String lockerAddress) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("quasar1035@gmail.com");
            helper.setTo(recipientEmail);
            helper.setSubject("📦 Tu paquete está listo para retirar - Servientrega");

            String htmlContent = buildEmailContent(
                recipientName,
                trackingNumber,
                retrievalCode.getCode(),
                retrievalCode.getSecretPin(),
                lockerName,
                lockerAddress,
                retrievalCode.getExpiresAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            );

            helper.setText(htmlContent, true);

            // Generar y adjuntar QR
            byte[] qrCode = qrCodeService.generateQRCode(retrievalCode.getCode(), 400, 400);
            helper.addAttachment("codigo-retiro-qr.png", new ByteArrayResource(qrCode));

            mailSender.send(message);
            log.info("Email sent successfully to {} for tracking: {}", recipientEmail, trackingNumber);

        } catch (Exception e) {
            log.error("Error sending email for tracking {}: {}", trackingNumber, e.getMessage());
        }
    }

    private String buildEmailContent(String recipientName, String trackingNumber, 
                                     String retrievalCode, String secretPin,
                                     String lockerName, String lockerAddress, 
                                     String expirationDate) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #e74c3c; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border: 1px solid #ddd; }
                    .info-box { background: white; padding: 15px; margin: 15px 0; border-left: 4px solid #e74c3c; }
                    .code-box { background: #fff3cd; padding: 20px; text-align: center; margin: 20px 0; border-radius: 5px; }
                    .code { font-size: 32px; font-weight: bold; color: #e74c3c; letter-spacing: 3px; }
                    .pin { font-size: 24px; font-weight: bold; color: #333; }
                    .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                    .highlight { color: #e74c3c; font-weight: bold; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎉 ¡Tu paquete llegó!</h1>
                        <p>Servientrega Casilleros Inteligentes</p>
                    </div>
                    
                    <div class="content">
                        <h2>Hola %s,</h2>
                        <p>Tu paquete ha sido depositado exitosamente en nuestro casillero inteligente y está listo para ser retirado.</p>
                        
                        <div class="info-box">
                            <h3>📋 Información del Paquete</h3>
                            <p><strong>Destinatario:</strong> %s</p>
                            <p><strong>Número de Guía:</strong> %s</p>
                            <p><strong>Ubicación:</strong> %s</p>
                            <p><strong>Dirección:</strong> %s</p>
                            <p><strong>Válido hasta:</strong> <span class="highlight">%s</span></p>
                        </div>
                        
                        <div class="code-box">
                            <h3>🔑 Tu Código de Retiro</h3>
                            <div class="code">%s</div>
                            <p style="margin-top: 15px;">PIN Secreto: <span class="pin">%s</span></p>
                            <p style="font-size: 12px; color: #666; margin-top: 10px;">
                                También puedes usar el código QR adjunto
                            </p>
                        </div>
                        
                        <div class="info-box">
                            <h3>📱 ¿Cómo retirar tu paquete?</h3>
                            <ol>
                                <li>Ve al casillero ubicado en: <strong>%s</strong></li>
                                <li>Escanea el código QR adjunto o ingresa el código: <strong>%s</strong></li>
                                <li>Ingresa el PIN: <strong>%s</strong></li>
                                <li>El casillero se abrirá automáticamente</li>
                                <li>Retira tu paquete</li>
                            </ol>
                        </div>
                        
                        <div style="background: #fff3cd; padding: 15px; border-radius: 5px; margin-top: 20px;">
                            <p style="margin: 0;"><strong>⚠️ Importante:</strong></p>
                            <ul style="margin: 10px 0;">
                                <li>Este código expira el <strong>%s</strong></li>
                                <li>Solo puedes usar el código una vez</li>
                                <li>No compartas tu código con nadie</li>
                            </ul>
                        </div>
                    </div>
                    
                    <div class="footer">
                        <p>Este es un correo automático, por favor no responder.</p>
                        <p>© 2026 Servientrega - Casilleros Inteligentes</p>
                        <p>Para soporte: soporte@servientrega.com</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                recipientName, recipientName, trackingNumber, lockerName, lockerAddress, 
                expirationDate, retrievalCode, secretPin, lockerAddress, retrievalCode, 
                secretPin, expirationDate
            );
    }
}

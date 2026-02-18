package com.servientrega.locker.service.notification;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TemplateService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public String getRetrievalCodeSmsTemplate(String code, String location, LocalDateTime expiresAt) {
        return String.format(
            "Servientrega: Tu paquete está en %s. Código de retiro: %s. Válido hasta: %s",
            location, code, expiresAt.format(FORMATTER)
        );
    }

    public String getRetrievalCodeEmailSubject() {
        return "Tu paquete está listo para retirar - Servientrega";
    }

    public String getRetrievalCodeEmailBody(String recipientName, String code, String location, 
                                           String address, LocalDateTime expiresAt) {
        return String.format("""
            Hola %s,
            
            Tu paquete ha llegado y está listo para ser retirado.
            
            Código de retiro: %s
            Ubicación: %s
            Dirección: %s
            Válido hasta: %s
            
            Instrucciones:
            1. Dirígete al locker en la ubicación indicada
            2. Ingresa tu código de retiro en la pantalla
            3. El casillero se abrirá automáticamente
            4. Retira tu paquete y cierra la puerta
            
            Gracias por usar Servientrega.
            """, recipientName, code, location, address, expiresAt.format(FORMATTER));
    }

    public String getExpirationAlertSmsTemplate(String code, int hoursRemaining) {
        return String.format(
            "Servientrega: Tu código %s expira en %d horas. Retira tu paquete pronto.",
            code, hoursRemaining
        );
    }

    public String getExpirationAlertEmailSubject() {
        return "Recordatorio: Tu código de retiro está por expirar - Servientrega";
    }

    public String getExpirationAlertEmailBody(String recipientName, String code, int hoursRemaining) {
        return String.format("""
            Hola %s,
            
            Te recordamos que tu código de retiro %s expirará en %d horas.
            
            Por favor, retira tu paquete lo antes posible para evitar inconvenientes.
            
            Gracias por usar Servientrega.
            """, recipientName, code, hoursRemaining);
    }

    public String getDeliveryConfirmationSmsTemplate(String trackingNumber) {
        return String.format(
            "Servientrega: Tu paquete %s ha sido entregado exitosamente. Gracias por tu preferencia.",
            trackingNumber
        );
    }

    public String getDeliveryConfirmationEmailSubject() {
        return "Paquete entregado - Servientrega";
    }

    public String getDeliveryConfirmationEmailBody(String recipientName, String trackingNumber, 
                                                   LocalDateTime deliveryTime) {
        return String.format("""
            Hola %s,
            
            Tu paquete con número de guía %s ha sido entregado exitosamente.
            
            Fecha y hora de entrega: %s
            
            Esperamos que disfrutes tu compra.
            
            Gracias por confiar en Servientrega.
            """, recipientName, trackingNumber, deliveryTime.format(FORMATTER));
    }

    public String getMaintenanceAlertEmailSubject(String lockerName) {
        return String.format("Alerta de mantenimiento - %s", lockerName);
    }

    public String getMaintenanceAlertEmailBody(String lockerName, String compartmentNumber, String issue) {
        return String.format("""
            Alerta de Mantenimiento
            
            Locker: %s
            Compartimento: %s
            Problema detectado: %s
            
            Se requiere atención técnica inmediata.
            
            Sistema de Gestión de Lockers Servientrega
            """, lockerName, compartmentNumber, issue);
    }
}

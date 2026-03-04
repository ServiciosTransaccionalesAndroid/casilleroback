package com.servientrega.locker.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PackageDetailDTO {
    
    public record PackageFullDetail(
        // Información del paquete
        Long id,
        String trackingNumber,
        String recipientName,
        String recipientPhone,
        String recipientEmail,
        BigDecimal width,
        BigDecimal height,
        BigDecimal depth,
        BigDecimal weight,
        String description,
        String status,
        LocalDateTime createdAt,
        
        // Información del depósito (si existe)
        DepositInfo deposit,
        
        // Información del retiro (si existe)
        RetrievalInfo retrieval
    ) {}
    
    public record DepositInfo(
        Long depositId,
        String courierName,
        String courierEmployeeId,
        Integer compartmentNumber,
        String compartmentSize,
        String lockerName,
        String lockerAddress,
        LocalDateTime depositTimestamp,
        String retrievalCode,
        String secretPin,
        LocalDateTime codeExpiresAt,
        boolean codeUsed
    ) {}
    
    public record RetrievalInfo(
        Long retrievalId,
        LocalDateTime retrievalTimestamp,
        String codeUsed
    ) {}
}

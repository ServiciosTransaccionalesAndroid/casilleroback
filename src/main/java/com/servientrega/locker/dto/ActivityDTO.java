package com.servientrega.locker.dto;

import java.time.LocalDateTime;

public class ActivityDTO {
    
    public record DepositDetail(
        Long id,
        String trackingNumber,
        String recipientName,
        String recipientPhone,
        String courierName,
        String courierEmployeeId,
        Integer compartmentNumber,
        String lockerName,
        LocalDateTime depositTimestamp,
        String photoUrl
    ) {}
    
    public record RetrievalDetail(
        Long id,
        String trackingNumber,
        String recipientName,
        String recipientPhone,
        Integer compartmentNumber,
        String lockerName,
        LocalDateTime retrievalTimestamp
    ) {}
}

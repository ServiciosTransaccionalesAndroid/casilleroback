package com.servientrega.locker.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record OperationHistoryDTO(
    Long id,
    String operationType,
    String entityType,
    Long entityId,
    String description,
    String userType,
    Long userId,
    String userName,
    Map<String, Object> metadata,
    LocalDateTime timestamp
) {}

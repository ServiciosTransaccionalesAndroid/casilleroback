package com.servientrega.locker.service;

import com.servientrega.locker.entity.Courier;
import com.servientrega.locker.entity.Deposit;
import com.servientrega.locker.entity.OperationLog;
import com.servientrega.locker.entity.Retrieval;
import com.servientrega.locker.entity.RetrievalCode;
import com.servientrega.locker.enums.EntityType;
import com.servientrega.locker.enums.OperationType;
import com.servientrega.locker.enums.PackageStatus;
import com.servientrega.locker.repository.OperationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OperationLogService {

    private final OperationLogRepository operationLogRepository;

    @Transactional
    public void logOperation(OperationType operationType, EntityType entityType, Long entityId,
                            String description, String userType, Long userId, Map<String, Object> metadata) {
        try {
            OperationLog operationLog = new OperationLog();
            operationLog.setOperationType(operationType);
            operationLog.setEntityType(entityType);
            operationLog.setEntityId(entityId);
            operationLog.setDescription(description);
            operationLog.setUserType(userType);
            operationLog.setUserId(userId);
            operationLog.setMetadata(metadata);
            
            operationLogRepository.save(operationLog);
            log.info("Operation logged: {} - {} - {}", operationType, entityType, description);
        } catch (Exception e) {
            log.error("Error logging operation: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public void logDeposit(Deposit deposit, Courier courier) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("depositId", deposit.getId());
        metadata.put("packageId", deposit.getPackageEntity().getId());
        metadata.put("trackingNumber", deposit.getPackageEntity().getTrackingNumber());
        metadata.put("compartmentId", deposit.getCompartment().getId());
        metadata.put("compartmentNumber", deposit.getCompartment().getCompartmentNumber());
        metadata.put("compartmentSize", deposit.getCompartment().getSize().name());
        metadata.put("lockerId", deposit.getCompartment().getLocker().getId());
        metadata.put("lockerName", deposit.getCompartment().getLocker().getName());
        
        String description = String.format("Paquete %s depositado en compartimento %d (%s) del %s por %s",
            deposit.getPackageEntity().getTrackingNumber(),
            deposit.getCompartment().getCompartmentNumber(),
            deposit.getCompartment().getSize().name(),
            deposit.getCompartment().getLocker().getName(),
            courier.getName()
        );
        
        logOperation(
            OperationType.DEPOSIT,
            EntityType.DEPOSIT,
            deposit.getId(),
            description,
            "COURIER",
            courier.getId(),
            metadata
        );
    }

    @Transactional
    public void logRetrieval(Retrieval retrieval) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("retrievalId", retrieval.getId());
        metadata.put("depositId", retrieval.getDeposit().getId());
        metadata.put("packageId", retrieval.getDeposit().getPackageEntity().getId());
        metadata.put("trackingNumber", retrieval.getDeposit().getPackageEntity().getTrackingNumber());
        metadata.put("code", retrieval.getRetrievalCode().getCode());
        metadata.put("compartmentNumber", retrieval.getDeposit().getCompartment().getCompartmentNumber());
        metadata.put("compartmentSize", retrieval.getDeposit().getCompartment().getSize().name());
        
        String description = String.format("Paquete %s retirado exitosamente con código %s del compartimento %d (%s)",
            retrieval.getDeposit().getPackageEntity().getTrackingNumber(),
            retrieval.getRetrievalCode().getCode(),
            retrieval.getDeposit().getCompartment().getCompartmentNumber(),
            retrieval.getDeposit().getCompartment().getSize().name()
        );
        
        logOperation(
            OperationType.RETRIEVAL,
            EntityType.RETRIEVAL,
            retrieval.getId(),
            description,
            "CLIENT",
            null,
            metadata
        );
    }

    @Transactional
    public void logStatusChange(com.servientrega.locker.entity.Package packageEntity, PackageStatus oldStatus, PackageStatus newStatus) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("packageId", packageEntity.getId());
        metadata.put("trackingNumber", packageEntity.getTrackingNumber());
        metadata.put("oldStatus", oldStatus.name());
        metadata.put("newStatus", newStatus.name());
        
        String description = String.format("Estado del paquete %s cambió de %s a %s",
            packageEntity.getTrackingNumber(),
            oldStatus.name(),
            newStatus.name()
        );
        
        logOperation(
            OperationType.STATUS_CHANGE,
            EntityType.PACKAGE,
            packageEntity.getId(),
            description,
            "SYSTEM",
            null,
            metadata
        );
    }

    @Transactional
    public void logCodeGeneration(RetrievalCode code, Deposit deposit) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("codeId", code.getId());
        metadata.put("code", code.getCode());
        metadata.put("secretPin", code.getSecretPin());
        metadata.put("depositId", deposit.getId());
        metadata.put("trackingNumber", deposit.getPackageEntity().getTrackingNumber());
        metadata.put("expiresAt", code.getExpiresAt().toString());
        
        String description = String.format("Código de retiro %s generado para paquete %s (PIN: %s)",
            code.getCode(),
            deposit.getPackageEntity().getTrackingNumber(),
            code.getSecretPin()
        );
        
        logOperation(
            OperationType.CODE_GENERATED,
            EntityType.RETRIEVAL_CODE,
            code.getId(),
            description,
            "SYSTEM",
            null,
            metadata
        );
    }

    public List<OperationLog> getEntityHistory(EntityType entityType, Long entityId) {
        return operationLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId);
    }

    public List<OperationLog> getOperationHistory(LocalDateTime start, LocalDateTime end, OperationType type) {
        if (type != null) {
            return operationLogRepository.findByOperationTypeAndCreatedAtBetweenOrderByCreatedAtDesc(type, start, end);
        }
        return operationLogRepository.findByDateRange(start, end);
    }
}

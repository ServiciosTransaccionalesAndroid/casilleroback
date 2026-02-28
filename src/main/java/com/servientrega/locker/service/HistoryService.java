package com.servientrega.locker.service;

import com.servientrega.locker.dto.OperationHistoryDTO;
import com.servientrega.locker.dto.PagedResponse;
import com.servientrega.locker.entity.OperationLog;
import com.servientrega.locker.enums.EntityType;
import com.servientrega.locker.enums.OperationType;
import com.servientrega.locker.repository.CourierRepository;
import com.servientrega.locker.repository.OperationLogRepository;
import com.servientrega.locker.repository.PackageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistoryService {

    private final OperationLogRepository operationLogRepository;
    private final PackageRepository packageRepository;
    private final CourierRepository courierRepository;

    public List<OperationHistoryDTO> getPackageHistory(String trackingNumber) {
        log.info("Getting history for package: {}", trackingNumber);
        
        var packageEntity = packageRepository.findByTrackingNumber(trackingNumber)
            .orElseThrow(() -> new RuntimeException("Package not found: " + trackingNumber));
        
        List<OperationLog> logs = operationLogRepository
            .findByEntityTypeAndEntityIdOrderByCreatedAtDesc(EntityType.PACKAGE, packageEntity.getId());
        
        return logs.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    public List<OperationHistoryDTO> getCompartmentHistory(Long compartmentId) {
        log.info("Getting history for compartment: {}", compartmentId);
        
        List<OperationLog> logs = operationLogRepository
            .findByEntityTypeAndEntityIdOrderByCreatedAtDesc(EntityType.COMPARTMENT, compartmentId);
        
        return logs.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    public List<OperationHistoryDTO> getCourierHistory(Long courierId, LocalDate startDate, LocalDate endDate) {
        log.info("Getting history for courier: {} from {} to {}", courierId, startDate, endDate);
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        List<OperationLog> logs = operationLogRepository
            .findByUserAndDateRange("COURIER", courierId, start, end);
        
        return logs.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    public List<OperationHistoryDTO> getLockerHistory(Long lockerId, LocalDate startDate, LocalDate endDate) {
        log.info("Getting history for locker: {} from {} to {}", lockerId, startDate, endDate);
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        List<OperationLog> logs = operationLogRepository
            .findByEntityTypeAndEntityIdOrderByCreatedAtDesc(EntityType.LOCKER, lockerId);
        
        return logs.stream()
            .filter(log -> !log.getCreatedAt().isBefore(start) && !log.getCreatedAt().isAfter(end))
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    public List<OperationHistoryDTO> getOperationsByDateRange(
            LocalDate startDate, 
            LocalDate endDate, 
            OperationType operationType) {
        log.info("Getting operations from {} to {}, type: {}", startDate, endDate, operationType);
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        List<OperationLog> logs;
        if (operationType != null) {
            logs = operationLogRepository
                .findByOperationTypeAndCreatedAtBetweenOrderByCreatedAtDesc(operationType, start, end);
        } else {
            logs = operationLogRepository.findByDateRange(start, end);
        }
        
        return logs.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    public PagedResponse<OperationHistoryDTO> getOperationsByDateRangePaged(
            LocalDate startDate, 
            LocalDate endDate, 
            OperationType operationType,
            int page,
            int size) {
        log.info("Getting paged operations from {} to {}, type: {}, page: {}, size: {}", 
            startDate, endDate, operationType, page, size);
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        List<OperationLog> allLogs;
        if (operationType != null) {
            allLogs = operationLogRepository
                .findByOperationTypeAndCreatedAtBetweenOrderByCreatedAtDesc(operationType, start, end);
        } else {
            allLogs = operationLogRepository.findByDateRange(start, end);
        }
        
        int totalElements = allLogs.size();
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalElements);
        
        List<OperationHistoryDTO> pagedContent = allLogs.subList(fromIndex, toIndex)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        
        return PagedResponse.of(pagedContent, page, size, totalElements);
    }

    private OperationHistoryDTO mapToDTO(OperationLog log) {
        String userName = null;
        
        if ("COURIER".equals(log.getUserType()) && log.getUserId() != null) {
            userName = courierRepository.findById(log.getUserId())
                .map(courier -> courier.getName())
                .orElse(null);
        }
        
        return new OperationHistoryDTO(
            log.getId(),
            log.getOperationType().name(),
            log.getEntityType().name(),
            log.getEntityId(),
            log.getDescription(),
            log.getUserType(),
            log.getUserId(),
            userName,
            log.getMetadata(),
            log.getCreatedAt()
        );
    }
}

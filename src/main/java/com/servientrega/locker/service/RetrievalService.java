package com.servientrega.locker.service;

import com.servientrega.locker.entity.Retrieval;
import com.servientrega.locker.entity.RetrievalCode;
import com.servientrega.locker.enums.CompartmentStatus;
import com.servientrega.locker.enums.PackageStatus;
import com.servientrega.locker.repository.RetrievalRepository;
import com.servientrega.locker.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RetrievalService {

    private final RetrievalRepository retrievalRepository;
    private final RetrievalCodeService retrievalCodeService;
    private final CompartmentService compartmentService;
    private final PackageService packageService;
    private final NotificationService notificationService;
    private final OperationLogService operationLogService;

    public RetrievalCode validateRetrievalCode(String code) {
        log.info("Validating retrieval code: {}", code);
        return retrievalCodeService.validateCode(code);
    }

    public RetrievalDTO.RetrievalCodeInfo getRetrievalCodeInfo(String trackingNumber) {
        log.info("Getting retrieval code info for tracking: {}", trackingNumber);
        
        RetrievalCode retrievalCode = retrievalCodeService.getActiveCodeByTrackingNumber(trackingNumber);
        
        return new RetrievalDTO.RetrievalCodeInfo(
            retrievalCode.getCode(),
            retrievalCode.getSecretPin(),
            trackingNumber,
            retrievalCode.getDeposit().getCompartment().getCompartmentNumber(),
            retrievalCode.getDeposit().getCompartment().getLocker().getName(),
            retrievalCode.getExpiresAt(),
            retrievalCode.getUsed()
        );
    }

    @Transactional
    public RetrievalResult processRetrieval(String code, String secretPin, String photoUrl) {
        log.info("Processing retrieval with code: {}", code);

        RetrievalCode retrievalCode = retrievalCodeService.validateCode(code);
        if (retrievalCode == null) {
            throw new RuntimeException("Invalid or expired retrieval code: " + code);
        }

        // Validar PIN secreto
        if (!retrievalCodeService.validateSecretPin(code, secretPin)) {
            log.warn("Invalid secret PIN for code: {}", code);
            throw new RuntimeException("Invalid secret PIN");
        }

        Retrieval retrieval = new Retrieval();
        retrieval.setDeposit(retrievalCode.getDeposit());
        retrieval.setRetrievalCode(retrievalCode);
        retrieval.setRetrievalTimestamp(LocalDateTime.now());
        retrieval.setPhotoUrl(photoUrl);

        Retrieval savedRetrieval = retrievalRepository.save(retrieval);
        log.info("Retrieval saved with ID: {}", savedRetrieval.getId());

        // Registrar operación en histórico
        operationLogService.logRetrieval(savedRetrieval);

        retrievalCodeService.markAsUsed(code);

        Long compartmentId = retrievalCode.getDeposit().getCompartment().getId();
        compartmentService.updateCompartmentStatus(compartmentId, CompartmentStatus.DISPONIBLE);

        String trackingNumber = retrievalCode.getDeposit().getPackageEntity().getTrackingNumber();
        packageService.updatePackageStatus(trackingNumber, PackageStatus.ENTREGADO);

        notificationService.sendDeliveryConfirmation(
            retrievalCode.getDeposit().getPackageEntity().getRecipientName(),
            retrievalCode.getDeposit().getPackageEntity().getRecipientPhone(),
            retrievalCode.getDeposit().getPackageEntity().getRecipientEmail(),
            trackingNumber,
            savedRetrieval.getRetrievalTimestamp()
        );

        log.info("Retrieval processed successfully");
        return new RetrievalResult(savedRetrieval.getId(), savedRetrieval.getRetrievalTimestamp());
    }

    public record RetrievalResult(Long retrievalId, LocalDateTime timestamp) {}
}

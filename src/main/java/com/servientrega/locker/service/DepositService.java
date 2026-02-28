package com.servientrega.locker.service;

import com.servientrega.locker.entity.*;
import com.servientrega.locker.enums.CompartmentStatus;
import com.servientrega.locker.enums.PackageStatus;
import com.servientrega.locker.repository.CourierRepository;
import com.servientrega.locker.repository.DepositRepository;
import com.servientrega.locker.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepositService {

    private final DepositRepository depositRepository;
    private final PackageService packageService;
    private final CompartmentService compartmentService;
    private final CourierRepository courierRepository;
    private final RetrievalCodeService retrievalCodeService;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final OperationLogService operationLogService;

    @Transactional
    public DepositResult processDeposit(String trackingNumber, Long compartmentId, Long courierId, String photoUrl) {
        log.info("Processing deposit - Tracking: {}, Compartment: {}, Courier: {}", 
            trackingNumber, compartmentId, courierId);

        com.servientrega.locker.entity.Package packageEntity = packageService.validatePackage(trackingNumber);
        if (packageEntity == null) {
            throw new RuntimeException("Package not found: " + trackingNumber);
        }

        Courier courier = courierRepository.findById(courierId)
            .orElseThrow(() -> new RuntimeException("Courier not found: " + courierId));

        Compartment compartment = compartmentService.getCompartmentsByLocker(1L).stream()
            .filter(c -> c.getId().equals(compartmentId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Compartment not found: " + compartmentId));

        Deposit deposit = new Deposit();
        deposit.setPackageEntity(packageEntity);
        deposit.setCompartment(compartment);
        deposit.setCourier(courier);
        deposit.setDepositTimestamp(LocalDateTime.now());
        deposit.setPhotoUrl(photoUrl);

        Deposit savedDeposit = depositRepository.save(deposit);
        log.info("Deposit saved with ID: {}", savedDeposit.getId());

        RetrievalCode retrievalCode = retrievalCodeService.generateCode(savedDeposit);

        // Registrar operación en histórico
        operationLogService.logDeposit(savedDeposit, courier);
        operationLogService.logCodeGeneration(retrievalCode, savedDeposit);

        compartmentService.updateCompartmentStatus(compartmentId, CompartmentStatus.OCUPADO);
        packageService.updatePackageStatus(trackingNumber, PackageStatus.EN_LOCKER);

        notificationService.sendRetrievalCodeNotification(
            packageEntity.getRecipientName(),
            packageEntity.getRecipientPhone(),
            packageEntity.getRecipientEmail(),
            retrievalCode.getCode(),
            compartment.getLocker().getName(),
            compartment.getLocker().getAddress(),
            retrievalCode.getExpiresAt()
        );

        // Enviar correo con QR
        emailService.sendRetrievalCodeEmail(
            retrievalCode,
            packageEntity.getRecipientEmail(),
            packageEntity.getRecipientName(),
            trackingNumber,
            compartment.getLocker().getName(),
            compartment.getLocker().getAddress()
        );

        log.info("Deposit processed successfully - Code: {}", retrievalCode.getCode());
        return new DepositResult(
            savedDeposit.getId(), 
            retrievalCode.getCode(), 
            retrievalCode.getSecretPin(),
            retrievalCode.getExpiresAt()
        );
    }

    public record DepositResult(Long depositId, String retrievalCode, String secretPin, LocalDateTime expiresAt) {}
}

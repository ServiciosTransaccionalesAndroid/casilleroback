package com.servientrega.locker.service;

import com.servientrega.locker.entity.Package;
import com.servientrega.locker.enums.PackageStatus;
import com.servientrega.locker.repository.PackageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PackageService {

    private final PackageRepository packageRepository;
    private final ErpSimulatorService erpSimulatorService;
    private final OperationLogService operationLogService;

    @Transactional
    public Package validatePackage(String trackingNumber) {
        log.info("Validating package: {}", trackingNumber);
        
        Package existingPackage = packageRepository.findByTrackingNumber(trackingNumber).orElse(null);
        if (existingPackage != null) {
            log.info("Package found in database: {}", trackingNumber);
            return existingPackage;
        }

        ErpSimulatorService.PackageData erpData = erpSimulatorService.validatePackage(trackingNumber);
        if (erpData == null) {
            log.warn("Package not found in ERP: {}", trackingNumber);
            return null;
        }

        Package newPackage = new Package();
        newPackage.setTrackingNumber(erpData.trackingNumber);
        newPackage.setRecipientName(erpData.recipientName);
        newPackage.setRecipientPhone(erpData.recipientPhone);
        newPackage.setRecipientEmail(erpData.recipientEmail);
        newPackage.setWidth(erpData.width);
        newPackage.setHeight(erpData.height);
        newPackage.setDepth(erpData.depth);
        newPackage.setWeight(erpData.weight);
        newPackage.setStatus(erpData.status);

        Package savedPackage = packageRepository.save(newPackage);
        log.info("Package saved to database: {}", trackingNumber);
        return savedPackage;
    }

    @Transactional
    public void updatePackageStatus(String trackingNumber, PackageStatus status) {
        log.info("Updating package {} to status: {}", trackingNumber, status);
        
        Package packageEntity = packageRepository.findByTrackingNumber(trackingNumber)
            .orElseThrow(() -> new RuntimeException("Package not found: " + trackingNumber));
        
        PackageStatus oldStatus = packageEntity.getStatus();
        packageEntity.setStatus(status);
        packageRepository.save(packageEntity);
        
        // Registrar cambio de estado en histórico
        operationLogService.logStatusChange(packageEntity, oldStatus, status);
        
        erpSimulatorService.updatePackageStatus(trackingNumber, status);
        log.info("Package status updated successfully");
    }

    public Package getPackageInfo(String trackingNumber) {
        return packageRepository.findByTrackingNumber(trackingNumber).orElse(null);
    }
}

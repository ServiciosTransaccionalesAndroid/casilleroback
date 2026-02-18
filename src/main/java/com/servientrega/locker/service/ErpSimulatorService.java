package com.servientrega.locker.service;

import com.servientrega.locker.enums.PackageStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ErpSimulatorService {

    private final Map<String, PackageData> mockPackages = new HashMap<>();

    public ErpSimulatorService() {
        initializeMockData();
    }

    private void initializeMockData() {
        mockPackages.put("SRV123456789", new PackageData(
            "SRV123456789", "Carlos Rodríguez", "+573101234567", 
            "carlos.rodriguez@email.com", new BigDecimal("30"), new BigDecimal("20"), 
            new BigDecimal("15"), new BigDecimal("2.5"), PackageStatus.EN_TRANSITO
        ));
        mockPackages.put("SRV987654321", new PackageData(
            "SRV987654321", "Ana Martínez", "+573109876543", 
            "ana.martinez@email.com", new BigDecimal("40"), new BigDecimal("30"), 
            new BigDecimal("25"), new BigDecimal("5.0"), PackageStatus.EN_TRANSITO
        ));
        mockPackages.put("SRV555666777", new PackageData(
            "SRV555666777", "Pedro Sánchez", "+573105556677", 
            "pedro.sanchez@email.com", new BigDecimal("20"), new BigDecimal("15"), 
            new BigDecimal("10"), new BigDecimal("1.0"), PackageStatus.EN_TRANSITO
        ));
    }

    public PackageData validatePackage(String trackingNumber) {
        log.info("[ERP SIMULATOR] Validating package: {}", trackingNumber);
        PackageData packageData = mockPackages.get(trackingNumber);
        
        if (packageData == null) {
            log.warn("[ERP SIMULATOR] Package not found: {}", trackingNumber);
            return null;
        }
        
        log.info("[ERP SIMULATOR] Package validated: {} - Status: {}", trackingNumber, packageData.status);
        return packageData;
    }

    public void updatePackageStatus(String trackingNumber, PackageStatus newStatus) {
        log.info("[ERP SIMULATOR] Updating package {} to status: {}", trackingNumber, newStatus);
        PackageData packageData = mockPackages.get(trackingNumber);
        
        if (packageData != null) {
            packageData.status = newStatus;
            log.info("[ERP SIMULATOR] Package {} updated successfully", trackingNumber);
        } else {
            log.warn("[ERP SIMULATOR] Package not found for update: {}", trackingNumber);
        }
    }

    public static class PackageData {
        public String trackingNumber;
        public String recipientName;
        public String recipientPhone;
        public String recipientEmail;
        public BigDecimal width;
        public BigDecimal height;
        public BigDecimal depth;
        public BigDecimal weight;
        public PackageStatus status;

        public PackageData(String trackingNumber, String recipientName, String recipientPhone,
                          String recipientEmail, BigDecimal width, BigDecimal height,
                          BigDecimal depth, BigDecimal weight, PackageStatus status) {
            this.trackingNumber = trackingNumber;
            this.recipientName = recipientName;
            this.recipientPhone = recipientPhone;
            this.recipientEmail = recipientEmail;
            this.width = width;
            this.height = height;
            this.depth = depth;
            this.weight = weight;
            this.status = status;
        }
    }
}

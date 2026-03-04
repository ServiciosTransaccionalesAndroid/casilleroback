package com.servientrega.locker.service;

import com.servientrega.locker.dto.PackageDTO;
import com.servientrega.locker.entity.Package;
import com.servientrega.locker.entity.Recipient;
import com.servientrega.locker.enums.PackageStatus;
import com.servientrega.locker.repository.PackageRepository;
import com.servientrega.locker.repository.RecipientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PackageService {

    private final PackageRepository packageRepository;
    private final RecipientRepository recipientRepository;
    private final RecipientService recipientService;
    private final EmailService emailService;
    private final com.servientrega.locker.repository.RetrievalCodeRepository retrievalCodeRepository;
    private final com.servientrega.locker.repository.DepositRepository depositRepository;
    private final com.servientrega.locker.repository.RetrievalRepository retrievalRepository;

    @Transactional
    public PackageDTO.PackageResponse create(PackageDTO.CreateRequest request) {
        if (packageRepository.findByTrackingNumber(request.trackingNumber()).isPresent()) {
            throw new RuntimeException("Tracking number already exists");
        }

        Recipient recipient = recipientRepository.findById(request.recipientId())
            .orElseThrow(() -> new RuntimeException("Recipient not found"));

        Package pkg = new Package();
        pkg.setTrackingNumber(request.trackingNumber());
        pkg.setRecipientName(recipient.getName());
        pkg.setRecipientPhone(recipient.getPhone());
        pkg.setRecipientEmail(recipient.getEmail());
        pkg.setWidth(request.width());
        pkg.setHeight(request.height());
        pkg.setDepth(request.depth());
        pkg.setWeight(request.weight());
        pkg.setDescription(request.description());
        pkg.setStatus(PackageStatus.EN_TRANSITO);

        Package saved = packageRepository.save(pkg);
        return toResponse(saved, recipient);
    }

    @Transactional
    public PackageDTO.PackageResponse update(Long id, PackageDTO.UpdateRequest request) {
        Package pkg = packageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Package not found"));

        if (request.recipientId() != null) {
            Recipient recipient = recipientRepository.findById(request.recipientId())
                .orElseThrow(() -> new RuntimeException("Recipient not found"));
            pkg.setRecipientName(recipient.getName());
            pkg.setRecipientPhone(recipient.getPhone());
            pkg.setRecipientEmail(recipient.getEmail());
        }

        if (request.width() != null) pkg.setWidth(request.width());
        if (request.height() != null) pkg.setHeight(request.height());
        if (request.depth() != null) pkg.setDepth(request.depth());
        if (request.weight() != null) pkg.setWeight(request.weight());
        if (request.description() != null) pkg.setDescription(request.description());
        if (request.status() != null) pkg.setStatus(PackageStatus.valueOf(request.status()));

        Package updated = packageRepository.save(pkg);
        Recipient recipient = recipientRepository.findByPhone(updated.getRecipientPhone()).orElse(null);
        return toResponse(updated, recipient);
    }

    @Transactional
    public void delete(Long id) {
        Package pkg = packageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Package not found"));
        packageRepository.delete(pkg);
    }

    public PackageDTO.PackageResponse getById(Long id) {
        Package pkg = packageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Package not found"));
        Recipient recipient = recipientRepository.findByPhone(pkg.getRecipientPhone()).orElse(null);
        return toResponse(pkg, recipient);
    }

    public List<PackageDTO.PackageResponse> getAll() {
        return packageRepository.findAll().stream()
            .map(pkg -> {
                Recipient recipient = recipientRepository.findByPhone(pkg.getRecipientPhone()).orElse(null);
                return toResponse(pkg, recipient);
            })
            .collect(Collectors.toList());
    }

    private PackageDTO.PackageResponse toResponse(Package pkg, Recipient recipient) {
        return new PackageDTO.PackageResponse(
            pkg.getId(),
            pkg.getTrackingNumber(),
            recipient != null ? recipientService.toResponse(recipient) : null,
            pkg.getWidth(),
            pkg.getHeight(),
            pkg.getDepth(),
            pkg.getWeight(),
            pkg.getDescription(),
            pkg.getStatus().name()
        );
    }

    public Package validatePackage(String trackingNumber) {
        return packageRepository.findByTrackingNumber(trackingNumber)
            .orElseThrow(() -> new RuntimeException("Package not found: " + trackingNumber));
    }

    @Transactional
    public void updatePackageStatus(String trackingNumber, PackageStatus status) {
        Package pkg = validatePackage(trackingNumber);
        pkg.setStatus(status);
        packageRepository.save(pkg);
    }

    @Transactional
    public void resendRetrievalCode(String trackingNumber) {
        Package pkg = validatePackage(trackingNumber);
        
        if (pkg.getStatus() != PackageStatus.EN_LOCKER) {
            throw new RuntimeException("Package is not in locker. Current status: " + pkg.getStatus());
        }

        com.servientrega.locker.entity.RetrievalCode retrievalCode = retrievalCodeRepository
            .findActiveByTrackingNumber(trackingNumber)
            .orElseThrow(() -> new RuntimeException("No active retrieval code found for package: " + trackingNumber));

        com.servientrega.locker.entity.Deposit deposit = depositRepository
            .findByPackageEntityId(pkg.getId())
            .orElseThrow(() -> new RuntimeException("Deposit not found for package: " + trackingNumber));

        emailService.sendRetrievalCodeEmail(
            retrievalCode,
            pkg.getRecipientEmail(),
            pkg.getRecipientName(),
            trackingNumber,
            deposit.getCompartment().getLocker().getName(),
            deposit.getCompartment().getLocker().getAddress()
        );
    }

    public com.servientrega.locker.dto.PackageDetailDTO.PackageFullDetail getPackageFullDetails(String trackingNumber) {
        Package pkg = validatePackage(trackingNumber);
        
        // Buscar depósito
        com.servientrega.locker.dto.PackageDetailDTO.DepositInfo depositInfo = null;
        var depositOpt = depositRepository.findByPackageEntityId(pkg.getId());
        if (depositOpt.isPresent()) {
            var deposit = depositOpt.get();
            var retrievalCodeOpt = retrievalCodeRepository.findAll().stream()
                .filter(rc -> rc.getDeposit().getId().equals(deposit.getId()))
                .findFirst();
            
            if (retrievalCodeOpt.isPresent()) {
                var code = retrievalCodeOpt.get();
                depositInfo = new com.servientrega.locker.dto.PackageDetailDTO.DepositInfo(
                    deposit.getId(),
                    deposit.getCourier().getName(),
                    deposit.getCourier().getEmployeeId(),
                    deposit.getCompartment().getCompartmentNumber(),
                    deposit.getCompartment().getSize().name(),
                    deposit.getCompartment().getLocker().getName(),
                    deposit.getCompartment().getLocker().getAddress(),
                    deposit.getDepositTimestamp(),
                    code.getCode(),
                    code.getSecretPin(),
                    code.getExpiresAt(),
                    code.getUsed()
                );
            }
        }
        
        // Buscar retiro
        com.servientrega.locker.dto.PackageDetailDTO.RetrievalInfo retrievalInfo = null;
        if (depositOpt.isPresent()) {
            var retrievalOpt = retrievalRepository.findByDepositId(depositOpt.get().getId());
            if (retrievalOpt.isPresent()) {
                var retrieval = retrievalOpt.get();
                retrievalInfo = new com.servientrega.locker.dto.PackageDetailDTO.RetrievalInfo(
                    retrieval.getId(),
                    retrieval.getRetrievalTimestamp(),
                    retrieval.getRetrievalCode().getCode()
                );
            }
        }
        
        return new com.servientrega.locker.dto.PackageDetailDTO.PackageFullDetail(
            pkg.getId(),
            pkg.getTrackingNumber(),
            pkg.getRecipientName(),
            pkg.getRecipientPhone(),
            pkg.getRecipientEmail(),
            pkg.getWidth(),
            pkg.getHeight(),
            pkg.getDepth(),
            pkg.getWeight(),
            pkg.getDescription(),
            pkg.getStatus().name(),
            pkg.getCreatedAt(),
            depositInfo,
            retrievalInfo
        );
    }
}

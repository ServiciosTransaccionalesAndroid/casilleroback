package com.servientrega.locker.service;

import com.servientrega.locker.dto.PackageDTO;
import com.servientrega.locker.entity.Package;
import com.servientrega.locker.entity.Recipient;
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
        pkg.setStatus("EN_TRANSITO");

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
        if (request.status() != null) pkg.setStatus(request.status());

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
            pkg.getStatus()
        );
    }
}

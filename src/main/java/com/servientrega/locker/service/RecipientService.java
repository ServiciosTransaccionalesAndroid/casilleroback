package com.servientrega.locker.service;

import com.servientrega.locker.dto.RecipientDTO;
import com.servientrega.locker.entity.Recipient;
import com.servientrega.locker.repository.RecipientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipientService {

    private final RecipientRepository recipientRepository;

    @Transactional
    public RecipientDTO.RecipientResponse create(RecipientDTO.CreateRequest request) {
        Recipient recipient = new Recipient();
        recipient.setName(request.name());
        recipient.setPhone(request.phone());
        recipient.setEmail(request.email());
        recipient.setAddress(request.address());

        Recipient saved = recipientRepository.save(recipient);
        return toResponse(saved);
    }

    @Transactional
    public RecipientDTO.RecipientResponse update(Long id, RecipientDTO.UpdateRequest request) {
        Recipient recipient = recipientRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Recipient not found"));

        if (request.name() != null) recipient.setName(request.name());
        if (request.phone() != null) recipient.setPhone(request.phone());
        if (request.email() != null) recipient.setEmail(request.email());
        if (request.address() != null) recipient.setAddress(request.address());

        Recipient updated = recipientRepository.save(recipient);
        return toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        Recipient recipient = recipientRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Recipient not found"));
        recipientRepository.delete(recipient);
    }

    public RecipientDTO.RecipientResponse getById(Long id) {
        Recipient recipient = recipientRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Recipient not found"));
        return toResponse(recipient);
    }

    public List<RecipientDTO.RecipientResponse> getAll() {
        return recipientRepository.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public RecipientDTO.RecipientResponse toResponse(Recipient recipient) {
        return new RecipientDTO.RecipientResponse(
            recipient.getId(),
            recipient.getName(),
            recipient.getPhone(),
            recipient.getEmail(),
            recipient.getAddress()
        );
    }
}

package com.servientrega.locker.service;

import com.servientrega.locker.entity.Deposit;
import com.servientrega.locker.entity.RetrievalCode;
import com.servientrega.locker.repository.RetrievalCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RetrievalCodeService {

    private final RetrievalCodeRepository retrievalCodeRepository;
    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Value("${app.retrieval-code.length:8}")
    private int codeLength;

    @Value("${app.retrieval-code.expiration-hours:48}")
    private int expirationHours;

    @Transactional
    public RetrievalCode generateCode(Deposit deposit) {
        log.info("Generating retrieval code for deposit: {}", deposit.getId());
        
        String code = generateUniqueCode();
        String secretPin = generateSecretPin();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusHours(expirationHours);

        RetrievalCode retrievalCode = new RetrievalCode();
        retrievalCode.setCode(code);
        retrievalCode.setSecretPin(secretPin);
        retrievalCode.setDeposit(deposit);
        retrievalCode.setGeneratedAt(now);
        retrievalCode.setExpiresAt(expiresAt);
        retrievalCode.setUsed(false);

        RetrievalCode saved = retrievalCodeRepository.save(retrievalCode);
        log.info("Retrieval code generated: {} - Secret PIN: {} - Expires at: {}", code, secretPin, expiresAt);
        return saved;
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = generateRandomCode();
        } while (retrievalCodeRepository.findByCode(code).isPresent());
        return code;
    }

    private String generateRandomCode() {
        StringBuilder code = new StringBuilder(codeLength);
        for (int i = 0; i < codeLength; i++) {
            code.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }

    private String generateSecretPin() {
        StringBuilder pin = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            pin.append(RANDOM.nextInt(10));
        }
        return pin.toString();
    }

    public RetrievalCode validateCode(String code) {
        log.info("Validating retrieval code: {}", code);
        
        RetrievalCode retrievalCode = retrievalCodeRepository.findByCode(code).orElse(null);
        if (retrievalCode == null) {
            log.warn("Retrieval code not found: {}", code);
            return null;
        }

        if (retrievalCode.getUsed()) {
            log.warn("Retrieval code already used: {}", code);
            return null;
        }

        if (LocalDateTime.now().isAfter(retrievalCode.getExpiresAt())) {
            log.warn("Retrieval code expired: {}", code);
            return null;
        }

        log.info("Retrieval code valid: {}", code);
        return retrievalCode;
    }

    public boolean validateSecretPin(String code, String secretPin) {
        log.info("Validating secret PIN for code: {}", code);
        
        RetrievalCode retrievalCode = retrievalCodeRepository.findByCode(code).orElse(null);
        if (retrievalCode == null) {
            log.warn("Retrieval code not found: {}", code);
            return false;
        }

        boolean isValid = secretPin.equals(retrievalCode.getSecretPin());
        log.info("Secret PIN validation result: {}", isValid);
        return isValid;
    }

    @Transactional
    public void markAsUsed(String code) {
        log.info("Marking retrieval code as used: {}", code);
        
        RetrievalCode retrievalCode = retrievalCodeRepository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Retrieval code not found: " + code));
        
        retrievalCode.setUsed(true);
        retrievalCode.setUsedAt(LocalDateTime.now());
        retrievalCodeRepository.save(retrievalCode);
        
        log.info("Retrieval code marked as used: {}", code);
    }
}

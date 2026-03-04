package com.servientrega.locker.service;

import com.servientrega.locker.dto.ActivityDTO;
import com.servientrega.locker.entity.Deposit;
import com.servientrega.locker.entity.Retrieval;
import com.servientrega.locker.repository.DepositRepository;
import com.servientrega.locker.repository.RetrievalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final DepositRepository depositRepository;
    private final RetrievalRepository retrievalRepository;

    public List<ActivityDTO.DepositDetail> getAllDeposits() {
        return depositRepository.findAllByOrderByDepositTimestampDesc().stream()
            .map(this::toDepositDetail)
            .collect(Collectors.toList());
    }

    public ActivityDTO.DepositDetail getDepositById(Long id) {
        Deposit deposit = depositRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Deposit not found"));
        return toDepositDetail(deposit);
    }

    public List<ActivityDTO.RetrievalDetail> getAllRetrievals() {
        return retrievalRepository.findAllByOrderByRetrievalTimestampDesc().stream()
            .map(this::toRetrievalDetail)
            .collect(Collectors.toList());
    }

    public ActivityDTO.RetrievalDetail getRetrievalById(Long id) {
        Retrieval retrieval = retrievalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Retrieval not found"));
        return toRetrievalDetail(retrieval);
    }

    private ActivityDTO.DepositDetail toDepositDetail(Deposit d) {
        return new ActivityDTO.DepositDetail(
            d.getId(),
            d.getPackageEntity().getTrackingNumber(),
            d.getPackageEntity().getRecipientName(),
            d.getPackageEntity().getRecipientPhone(),
            d.getCourier().getName(),
            d.getCourier().getEmployeeId(),
            d.getCompartment().getCompartmentNumber(),
            d.getCompartment().getSize().name(),
            d.getCompartment().getLocker().getName(),
            d.getDepositTimestamp(),
            d.getPhotoUrl()
        );
    }

    private ActivityDTO.RetrievalDetail toRetrievalDetail(Retrieval r) {
        return new ActivityDTO.RetrievalDetail(
            r.getId(),
            r.getDeposit().getPackageEntity().getTrackingNumber(),
            r.getDeposit().getPackageEntity().getRecipientName(),
            r.getDeposit().getPackageEntity().getRecipientPhone(),
            r.getDeposit().getCompartment().getCompartmentNumber(),
            r.getDeposit().getCompartment().getLocker().getName(),
            r.getRetrievalTimestamp()
        );
    }
}

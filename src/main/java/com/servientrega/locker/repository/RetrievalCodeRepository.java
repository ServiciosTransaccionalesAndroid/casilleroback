package com.servientrega.locker.repository;

import com.servientrega.locker.entity.RetrievalCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RetrievalCodeRepository extends JpaRepository<RetrievalCode, Long> {
    Optional<RetrievalCode> findByCode(String code);
    
    @Query("SELECT rc FROM RetrievalCode rc WHERE rc.deposit.packageEntity.trackingNumber = :trackingNumber AND rc.used = false ORDER BY rc.generatedAt DESC")
    Optional<RetrievalCode> findActiveByTrackingNumber(String trackingNumber);
}

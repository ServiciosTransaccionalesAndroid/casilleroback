package com.servientrega.locker.repository;

import com.servientrega.locker.entity.RetrievalCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RetrievalCodeRepository extends JpaRepository<RetrievalCode, Long> {
    Optional<RetrievalCode> findByCode(String code);
}

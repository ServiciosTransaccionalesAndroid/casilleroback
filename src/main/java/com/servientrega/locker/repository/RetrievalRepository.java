package com.servientrega.locker.repository;

import com.servientrega.locker.entity.Retrieval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RetrievalRepository extends JpaRepository<Retrieval, Long> {
    int countByRetrievalTimestampBetween(LocalDateTime start, LocalDateTime end);
    List<Retrieval> findAllByOrderByRetrievalTimestampDesc();
    Optional<Retrieval> findByDepositId(Long depositId);
}

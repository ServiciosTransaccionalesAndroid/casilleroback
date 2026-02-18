package com.servientrega.locker.repository;

import com.servientrega.locker.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByLockerIdAndResolvedAtIsNull(Long lockerId);
}

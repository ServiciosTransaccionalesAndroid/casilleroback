package com.servientrega.locker.repository;

import com.servientrega.locker.entity.Compartment;
import com.servientrega.locker.enums.CompartmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompartmentRepository extends JpaRepository<Compartment, Long> {
    List<Compartment> findByLockerIdAndStatus(Long lockerId, CompartmentStatus status);
    List<Compartment> findByLockerId(Long lockerId);
}

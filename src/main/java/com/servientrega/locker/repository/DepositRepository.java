package com.servientrega.locker.repository;

import com.servientrega.locker.entity.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, Long> {
    Optional<Deposit> findByPackageEntityId(Long packageId);
}

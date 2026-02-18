package com.servientrega.locker.repository;

import com.servientrega.locker.entity.Locker;
import com.servientrega.locker.enums.LockerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LockerRepository extends JpaRepository<Locker, Long> {
    List<Locker> findByStatus(LockerStatus status);
}

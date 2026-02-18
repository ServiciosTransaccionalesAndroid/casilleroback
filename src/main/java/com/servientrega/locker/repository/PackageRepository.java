package com.servientrega.locker.repository;

import com.servientrega.locker.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {
    Optional<Package> findByTrackingNumber(String trackingNumber);
}

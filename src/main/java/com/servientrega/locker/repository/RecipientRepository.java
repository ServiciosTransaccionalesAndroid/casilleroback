package com.servientrega.locker.repository;

import com.servientrega.locker.entity.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipientRepository extends JpaRepository<Recipient, Long> {
    Optional<Recipient> findByPhone(String phone);
}

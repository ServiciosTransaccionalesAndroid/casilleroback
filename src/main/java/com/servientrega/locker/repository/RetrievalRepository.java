package com.servientrega.locker.repository;

import com.servientrega.locker.entity.Retrieval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RetrievalRepository extends JpaRepository<Retrieval, Long> {
}

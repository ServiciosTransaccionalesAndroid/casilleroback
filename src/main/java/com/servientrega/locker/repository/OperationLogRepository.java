package com.servientrega.locker.repository;

import com.servientrega.locker.entity.OperationLog;
import com.servientrega.locker.enums.EntityType;
import com.servientrega.locker.enums.OperationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
    
    List<OperationLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(EntityType entityType, Long entityId);
    
    List<OperationLog> findByOperationTypeAndCreatedAtBetweenOrderByCreatedAtDesc(
        OperationType operationType, 
        LocalDateTime start, 
        LocalDateTime end
    );
    
    @Query("SELECT ol FROM OperationLog ol WHERE ol.createdAt BETWEEN :start AND :end ORDER BY ol.createdAt DESC")
    List<OperationLog> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT ol FROM OperationLog ol WHERE ol.userType = :userType AND ol.userId = :userId " +
           "AND ol.createdAt BETWEEN :start AND :end ORDER BY ol.createdAt DESC")
    List<OperationLog> findByUserAndDateRange(
        @Param("userType") String userType,
        @Param("userId") Long userId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
}

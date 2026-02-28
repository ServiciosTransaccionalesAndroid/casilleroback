package com.servientrega.locker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.servientrega.locker.entity.Deposit;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface ReportRepository extends JpaRepository<Deposit, Long> {
    
    @Query("""
        SELECT DATE(d.depositTimestamp) as date, COUNT(*) as count
        FROM Deposit d
        WHERE d.depositTimestamp BETWEEN :start AND :end
        AND (:lockerId IS NULL OR d.compartment.locker.id = :lockerId)
        GROUP BY DATE(d.depositTimestamp)
        ORDER BY date
    """)
    List<Map<String, Object>> getDepositsByDay(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end,
        @Param("lockerId") Long lockerId
    );
    
    @Query("""
        SELECT DATE(r.retrievalTimestamp) as date, COUNT(*) as count
        FROM Retrieval r
        WHERE r.retrievalTimestamp BETWEEN :start AND :end
        AND (:lockerId IS NULL OR r.deposit.compartment.locker.id = :lockerId)
        GROUP BY DATE(r.retrievalTimestamp)
        ORDER BY date
    """)
    List<Map<String, Object>> getRetrievalsByDay(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end,
        @Param("lockerId") Long lockerId
    );
    
    @Query("""
        SELECT c.size as size,
               COUNT(*) as total,
               SUM(CASE WHEN c.status = 'OCUPADO' THEN 1 ELSE 0 END) as occupied,
               SUM(CASE WHEN c.status = 'DISPONIBLE' THEN 1 ELSE 0 END) as available
        FROM Compartment c
        WHERE c.locker.id = :lockerId
        GROUP BY c.size
    """)
    List<Map<String, Object>> getOccupancyBySize(@Param("lockerId") Long lockerId);
    
    @Query("""
        SELECT rc.code as code,
               rc.expiresAt as expiresAt,
               p.trackingNumber as trackingNumber,
               p.recipientName as recipientName
        FROM RetrievalCode rc
        JOIN rc.deposit d
        JOIN d.packageEntity p
        WHERE rc.used = false
        AND rc.expiresAt BETWEEN :start AND :end
        AND rc.expiresAt < CURRENT_TIMESTAMP
    """)
    List<Map<String, Object>> getExpiredCodes(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
    
    @Query("""
        SELECT co.name as courierName,
               co.id as courierId,
               COUNT(d.id) as totalDeposits,
               DATE(d.depositTimestamp) as date
        FROM Deposit d
        JOIN d.courier co
        WHERE d.depositTimestamp BETWEEN :start AND :end
        GROUP BY co.name, co.id, DATE(d.depositTimestamp)
        ORDER BY date, totalDeposits DESC
    """)
    List<Map<String, Object>> getCourierPerformance(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
    
    @Query("""
        SELECT co.name as courierName,
               co.id as courierId,
               COUNT(d.id) as totalDeposits
        FROM Deposit d
        JOIN d.courier co
        WHERE d.depositTimestamp BETWEEN :start AND :end
        GROUP BY co.name, co.id
        ORDER BY totalDeposits DESC
    """)
    List<Map<String, Object>> getDepositsByCourier(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
    
    @Query("""
        SELECT c.id as compartmentId,
               c.compartmentNumber as compartmentNumber,
               c.size as size,
               c.status as status,
               COUNT(d.id) as timesUsed
        FROM Compartment c
        LEFT JOIN Deposit d ON d.compartment.id = c.id
        WHERE c.locker.id = :lockerId
        GROUP BY c.id, c.compartmentNumber, c.size, c.status
        ORDER BY c.compartmentNumber
    """)
    List<Map<String, Object>> getCompartmentUsage(@Param("lockerId") Long lockerId);
    
    @Query("""
        SELECT p.trackingNumber as trackingNumber,
               p.recipientName as recipientName,
               d.depositTimestamp as depositedAt,
               c.compartmentNumber as compartmentNumber,
               rc.code as retrievalCode
        FROM Deposit d
        JOIN d.packageEntity p
        JOIN d.compartment c
        LEFT JOIN RetrievalCode rc ON rc.deposit.id = d.id
        WHERE c.locker.id = :lockerId
        AND p.status = 'EN_LOCKER'
        ORDER BY d.depositTimestamp DESC
    """)
    List<Map<String, Object>> getActivePackages(@Param("lockerId") Long lockerId);
}

package com.servientrega.locker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "status_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compartment_id", nullable = false)
    private Compartment compartment;
    
    @Column(name = "previous_state", nullable = false, length = 20)
    private String previousState;
    
    @Column(name = "current_state", nullable = false, length = 20)
    private String currentState;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sensor_readings", columnDefinition = "jsonb")
    private Map<String, Object> sensorReadings;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}

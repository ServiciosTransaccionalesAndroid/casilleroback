package com.servientrega.locker.entity;

import com.servientrega.locker.enums.CompartmentSize;
import com.servientrega.locker.enums.CompartmentStatus;
import com.servientrega.locker.enums.DoorState;
import com.servientrega.locker.enums.PhysicalCondition;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "compartments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Compartment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locker_id", nullable = false)
    private Locker locker;
    
    @Column(name = "compartment_number", nullable = false)
    private Integer compartmentNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CompartmentSize size;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CompartmentStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "door_state", nullable = false, length = 20)
    private DoorState doorState;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "physical_condition", nullable = false, length = 30)
    private PhysicalCondition physicalCondition;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sensor_readings", columnDefinition = "jsonb")
    private Map<String, Object> sensorReadings;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

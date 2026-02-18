package com.servientrega.locker.entity;

import com.servientrega.locker.enums.LockerStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lockers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Locker {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, length = 200)
    private String location;
    
    @Column(length = 300)
    private String address;
    
    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;
    
    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LockerStatus status;
    
    @Column(name = "total_compartments")
    private Integer totalCompartments;
    
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

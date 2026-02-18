package com.servientrega.locker.entity;

import com.servientrega.locker.enums.PackageStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "packages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Package {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tracking_number", nullable = false, unique = true, length = 50)
    private String trackingNumber;
    
    @Column(name = "recipient_name", nullable = false, length = 200)
    private String recipientName;
    
    @Column(name = "recipient_phone", nullable = false, length = 20)
    private String recipientPhone;
    
    @Column(name = "recipient_email", length = 100)
    private String recipientEmail;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal width;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal height;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal depth;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal weight;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PackageStatus status;
    
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

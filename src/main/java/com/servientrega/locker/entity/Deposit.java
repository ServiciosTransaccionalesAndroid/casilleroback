package com.servientrega.locker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "deposits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Deposit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private Package packageEntity;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compartment_id", nullable = false)
    private Compartment compartment;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id", nullable = false)
    private Courier courier;
    
    @Column(name = "deposit_timestamp", nullable = false)
    private LocalDateTime depositTimestamp;
    
    @Column(name = "photo_url", length = 500)
    private String photoUrl;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (depositTimestamp == null) {
            depositTimestamp = LocalDateTime.now();
        }
    }
}

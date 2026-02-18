package com.servientrega.locker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "retrieval_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetrievalCode {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 10)
    private String code;
    
    @Column(name = "secret_pin", length = 6)
    private String secretPin;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deposit_id", nullable = false)
    private Deposit deposit;
    
    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(nullable = false)
    private Boolean used = false;
    
    @Column(name = "used_at")
    private LocalDateTime usedAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (generatedAt == null) {
            generatedAt = LocalDateTime.now();
        }
    }
}

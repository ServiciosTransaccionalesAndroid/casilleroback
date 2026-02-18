package com.servientrega.locker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "retrievals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Retrieval {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deposit_id", nullable = false)
    private Deposit deposit;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retrieval_code_id", nullable = false)
    private RetrievalCode retrievalCode;
    
    @Column(name = "retrieval_timestamp", nullable = false)
    private LocalDateTime retrievalTimestamp;
    
    @Column(name = "photo_url", length = 500)
    private String photoUrl;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (retrievalTimestamp == null) {
            retrievalTimestamp = LocalDateTime.now();
        }
    }
}

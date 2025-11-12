package com.soa.otp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "otps", indexes = {
    @Index(name = "idx_otp_customer", columnList = "customer_id"),
    @Index(name = "idx_otp_created_at", columnList = "created_at")
})
@Getter
@Setter
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(nullable = false, length = 6)
    private String code;

    @Column(name = "is_used", nullable = false)
    private boolean used = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    public Otp() {
        this.createdAt = LocalDateTime.now();
    }

    public Otp(Long customerId, String code) {
        this.customerId = customerId;
        this.code = code;
        this.used = false;
        this.createdAt = LocalDateTime.now();
    }
}

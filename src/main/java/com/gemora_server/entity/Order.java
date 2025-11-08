package com.gemora_server.entity;

import com.gemora_server.enums.ListingType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    private User buyer;

    @ManyToOne(optional = false)
    private Gem gem;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private ListingType Status ;
}

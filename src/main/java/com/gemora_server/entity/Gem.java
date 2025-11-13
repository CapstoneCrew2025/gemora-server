package com.gemora_server.entity;

import com.gemora_server.enums.GemStatus;
import com.gemora_server.enums.ListingType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gems")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String category;
    private Double carat;
    private String origin;
    private String certificationNumber; // optional summary field

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private GemStatus status = GemStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private ListingType listingType = ListingType.SALE;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    @Builder.Default
    @OneToMany(mappedBy = "gem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GemImage> images = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "gem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Certificate> certificates = new ArrayList<>();

    @OneToMany(mappedBy = "gem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bid> bids = new ArrayList<>();

  //  private Double startingPrice;
    private Double currentHighestBid;
    private LocalDateTime auctionEndTime;


}

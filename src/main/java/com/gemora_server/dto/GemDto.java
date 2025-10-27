package com.gemora_server.dto;

import com.gemora_server.enums.GemStatus;
import com.gemora_server.enums.ListingType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class GemDto {
    private Long id;
    private String name;
    private String description;
    private String category;
    private Double carat;
    private String origin;
    private String certificationNumber;
    private BigDecimal price;
    private GemStatus status;
    private ListingType listingType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long sellerId;
    private List<String> imageUrls;
    private List<CertificateDto> certificates;

}

package com.gemora_server.dto;

import com.gemora_server.enums.ListingType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class GemCreateRequest {
    private String name;
    private String description;
    private String category;
    private Double carat;
    private String origin;
    private String certificationNumber;
    private BigDecimal price;
    private ListingType listingType;



}

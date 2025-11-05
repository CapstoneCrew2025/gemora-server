package com.gemora_server.dto;

import com.gemora_server.enums.ListingType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GemUpdateRequestDto {
    private String name;
    private String description;
    private String category;
    private Double carat;
    private String origin;
    private BigDecimal price;
    private ListingType listingType;
    private List<Long> removeImageIds;
    private String certificateNumber;
    private String issuingAuthority;
    private String issueDate;
}

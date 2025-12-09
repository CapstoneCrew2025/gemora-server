package com.gemora_server.dto;

import lombok.Data;

@Data
public class PredictResponseDto {
    private double confidence;
    private String gem_type;
}

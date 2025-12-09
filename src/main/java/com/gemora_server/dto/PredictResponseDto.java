package com.gemora_server.dto;

import lombok.Data;

@Data
public class PredictResponseDto {
    private boolean success = true;
    private double confidence;
    private String gem_type;
    private String error;
}

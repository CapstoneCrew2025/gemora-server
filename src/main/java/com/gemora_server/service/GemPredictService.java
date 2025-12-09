package com.gemora_server.service;

import com.gemora_server.dto.PredictResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface GemPredictService {
    PredictResponseDto predict(MultipartFile file);

}

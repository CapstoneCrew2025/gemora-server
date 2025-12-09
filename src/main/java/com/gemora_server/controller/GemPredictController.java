package com.gemora_server.controller;

import com.gemora_server.dto.PredictResponseDto;
import com.gemora_server.service.GemPredictService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin("*")
public class GemPredictController {

    private final GemPredictService gemPredictService;

    @PostMapping("/predict")
    public PredictResponseDto predictGem(@RequestParam("file") MultipartFile file) throws Exception {
        return gemPredictService.predict(file);
    }

}

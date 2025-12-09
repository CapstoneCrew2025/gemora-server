package com.gemora_server.service.impl;

import org.springframework.http.HttpHeaders;
import com.gemora_server.dto.PredictResponseDto;
import com.gemora_server.service.GemPredictService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class GemPredictServiceImpl implements GemPredictService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String FLASK_URL = "http://localhost:5001/predict";

    @Override
    public PredictResponseDto predict(MultipartFile file) {

        try {
            ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileAsResource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, headers);

            ResponseEntity<PredictResponseDto> response =
                    restTemplate.exchange(
                            FLASK_URL,
                            HttpMethod.POST,
                            requestEntity,
                            PredictResponseDto.class
                    );

            return response.getBody();
        } catch (Exception ex) {
            PredictResponseDto error = new PredictResponseDto();
            error.setSuccess(false);
            error.setConfidence(0);
            error.setGem_type(null);
            error.setError("AI model is unavailable. Please try again later.");
            return error;
        }
    }


}
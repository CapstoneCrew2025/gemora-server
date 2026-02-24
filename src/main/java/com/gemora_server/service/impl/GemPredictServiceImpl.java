package com.gemora_server.service.impl;

import com.gemora_server.exception.BusinessException;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class GemPredictServiceImpl implements GemPredictService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public PredictResponseDto predict(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BusinessException("Image file is required for prediction");
        }

        ByteArrayResource fileAsResource;
        try {
            fileAsResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
        } catch (Exception ex) {
            throw new BusinessException("Failed to read uploaded file");
        }

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileAsResource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity =
                new HttpEntity<>(body, headers);

        try {
            String FLASK_URL = "http://localhost:5001/predict";
            ResponseEntity<PredictResponseDto> response =
                    restTemplate.exchange(
                            FLASK_URL,
                            HttpMethod.POST,
                            requestEntity,
                            PredictResponseDto.class
                    );

            if (response.getBody() == null) {
                throw new BusinessException("Empty response from prediction service");
            }

            return response.getBody();

        } catch (RestClientException ex) {
            throw new BusinessException(
                    "AI prediction service is unavailable. Please try again later"
            );
        }
        
    }
    
}
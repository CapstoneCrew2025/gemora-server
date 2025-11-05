package com.gemora_server.service;

import com.gemora_server.dto.GemCreateRequest;
import com.gemora_server.dto.GemDto;
import com.gemora_server.dto.GemUpdateRequestDto;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface GemService {

    GemDto createGem(Long sellerId, GemCreateRequest request, List<MultipartFile> images,MultipartFile certificateFile);
    List<GemDto> getMyGems(Long sellerId);
    List<GemDto> getApprovedGems();
    List<GemDto> getAllGemsByStatus(String status);
    GemDto getGem(Long id);
    GemDto approveGem(Long gemId, String adminUsername);
    GemDto rejectGem(Long gemId, String adminUsername, String reason);
    void deleteGem(Long gemId, Long sellerId);
    GemDto updateGem(Long gemId, Long sellerId, GemUpdateRequestDto req, List<MultipartFile> newImages, MultipartFile certificateFile);

    // certificate-specific
    GemDto uploadCertificate(Long gemId, MultipartFile certificateFile, String certificateNumber, String issuingAuthority, String issueDate); // issueDate as ISO string
    void verifyCertificate(Long certificateId, boolean verified, String adminUsername);
    void deleteGemAsAdmin(Long gemId, String adminUsername);


}

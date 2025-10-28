package com.gemora_server.service.impl;

import com.gemora_server.dto.CertificateDto;
import com.gemora_server.dto.GemCreateRequest;
import com.gemora_server.dto.GemDto;
import com.gemora_server.entity.Certificate;
import com.gemora_server.entity.Gem;
import com.gemora_server.entity.GemImage;
import com.gemora_server.entity.User;
import com.gemora_server.enums.GemStatus;
import com.gemora_server.repo.CertificateRepo;
import com.gemora_server.repo.GemImageRepo;
import com.gemora_server.repo.GemRepo;
import com.gemora_server.repo.UserRepo;
import com.gemora_server.service.FileStorageService;
import com.gemora_server.service.GemService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GemServiceImpl implements GemService {

    private final GemRepo gemRepo;
    private final UserRepo userRepo;
    private final GemImageRepo gemImageRepo;
    private final CertificateRepo certificateRepo;
    private final FileStorageService fileStorageService;

    public GemServiceImpl(GemRepo gemRepo,
                          UserRepo userRepo,
                          GemImageRepo gemImageRepo,
                          CertificateRepo certificateRepo,
                          FileStorageService fileStorageService) {
        this.gemRepo = gemRepo;
        this.userRepo = userRepo;
        this.gemImageRepo = gemImageRepo;
        this.certificateRepo = certificateRepo;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional
    public GemDto createGem(Long sellerId, GemCreateRequest request, List<MultipartFile> images) {
        User seller = userRepo.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        Gem gem = Gem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .carat(request.getCarat())
                .origin(request.getOrigin())
                .certificationNumber(request.getCertificationNumber())
                .price(request.getPrice())
                .listingType(request.getListingType() == null ? com.gemora_server.enums.ListingType.SALE : request.getListingType())
                .seller(seller)
                .status(GemStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Gem saved = gemRepo.save(gem);

        if (images != null) {
            if (saved.getImages() == null) {
                saved.setImages(new ArrayList<>());
            }
            for (MultipartFile img : images) {
                String fileName = fileStorageService.storeGemImage(img);
                Path p = fileStorageService.getFilePath(fileName, false);
                String url = "/uploads/gems/" + fileName; // aligned with WebConfig mapping
                GemImage gemImage = GemImage.builder()
                        .fileName(fileName)
                        .fileUrl(url)
                        .gem(saved)
                        .build();
                gemImageRepo.save(gemImage);
                saved.getImages().add(gemImage);
            }
        }

        return mapToDto(saved);
    }

    @Override
    public List<GemDto> getMyGems(Long sellerId) {
        User seller = userRepo.findById(sellerId).orElseThrow(() -> new RuntimeException("Seller not found"));
        return gemRepo.findBySeller(seller).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<GemDto> getApprovedGems() {
        return gemRepo.findByStatus(GemStatus.APPROVED).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public GemDto getGem(Long id) {
        Gem gem = gemRepo.findById(id).orElseThrow(() -> new RuntimeException("Gem not found"));
        return mapToDto(gem);
    }

    @Override
    @Transactional
    public GemDto approveGem(Long gemId, String adminUsername) {
        Gem gem = gemRepo.findById(gemId).orElseThrow(() -> new RuntimeException("Gem not found"));
        gem.setStatus(GemStatus.APPROVED);
        gem.setUpdatedAt(LocalDateTime.now());
        Gem saved = gemRepo.save(gem);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public GemDto rejectGem(Long gemId, String adminUsername, String reason) {
        Gem gem = gemRepo.findById(gemId).orElseThrow(() -> new RuntimeException("Gem not found"));
        gem.setStatus(GemStatus.REJECTED);
        gem.setUpdatedAt(LocalDateTime.now());
        Gem saved = gemRepo.save(gem);
        // TODO: persist rejection reason or notify seller via notification service
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void deleteGem(Long gemId, Long sellerId) {
        Gem gem = gemRepo.findById(gemId).orElseThrow(() -> new RuntimeException("Gem not found"));
        if (!gem.getSeller().getId().equals(sellerId)) {
            throw new RuntimeException("Not authorized to delete");
        }
        gemRepo.delete(gem);
    }

    @Override
    @Transactional
    public GemDto uploadCertificate(Long gemId, MultipartFile certificateFile, String certificateNumber, String issuingAuthority, String issueDate) {
        Gem gem = gemRepo.findById(gemId).orElseThrow(() -> new RuntimeException("Gem not found"));
        String fileName = fileStorageService.storeCertificateFile(certificateFile);
        String url = "/files/certificates/" + fileName;
        LocalDate parsedIssueDate = null;
        try {
            if (issueDate != null) parsedIssueDate = LocalDate.parse(issueDate);
        } catch (Exception ignored) {}

        Certificate cert = Certificate.builder()
                .certificateNumber(certificateNumber)
                .issuingAuthority(issuingAuthority)
                .issueDate(parsedIssueDate)
                .fileName(fileName)
                .fileUrl(url)
                .verified(false)
                .uploadedAt(LocalDateTime.now())
                .gem(gem)
                .build();
        certificateRepo.save(cert);
        gem.getCertificates().add(cert);
        gemRepo.save(gem);
        return mapToDto(gem);
    }

    @Override
    @Transactional
    public void verifyCertificate(Long certificateId, boolean verified, String adminUsername) {
        Certificate cert = certificateRepo.findById(certificateId).orElseThrow(() -> new RuntimeException("Certificate not found"));
        cert.setVerified(verified);
        cert.setVerifiedBy(adminUsername);
        cert.setVerifiedAt(LocalDateTime.now());
        certificateRepo.save(cert);
    }


    @Override
    public List<GemDto> getAllGemsByStatus(String status) {
        GemStatus gemStatus = GemStatus.valueOf(status.toUpperCase());
        return gemRepo.findByStatus(gemStatus)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }



    // helper mapping
    private GemDto mapToDto(Gem gem) {
        List<String> imageUrls = gem.getImages().stream().map(GemImage::getFileUrl).collect(Collectors.toList());
        List<CertificateDto> certs = gem.getCertificates().stream().map(c -> CertificateDto.builder()
                .id(c.getId())
                .certificateNumber(c.getCertificateNumber())
                .issuingAuthority(c.getIssuingAuthority())
                .issueDate(c.getIssueDate())
                .fileUrl(c.getFileUrl())
                .verified(c.getVerified())
                .uploadedAt(c.getUploadedAt())
                .verifiedAt(c.getVerifiedAt())
                .build()).collect(Collectors.toList());

        return GemDto.builder()
                .id(gem.getId())
                .name(gem.getName())
                .description(gem.getDescription())
                .category(gem.getCategory())
                .carat(gem.getCarat())
                .origin(gem.getOrigin())
                .certificationNumber(gem.getCertificationNumber())
                .price(gem.getPrice())
                .status(gem.getStatus())
                .listingType(gem.getListingType())
                .createdAt(gem.getCreatedAt())
                .updatedAt(gem.getUpdatedAt())
                .sellerId(gem.getSeller() != null ? gem.getSeller().getId() : null)
                .imageUrls(imageUrls)
                .certificates(certs)
                .build();
    }



}

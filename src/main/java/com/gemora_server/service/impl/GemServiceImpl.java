package com.gemora_server.service.impl;

import com.gemora_server.dto.CertificateDto;
import com.gemora_server.dto.GemCreateRequest;
import com.gemora_server.dto.GemDto;
import com.gemora_server.dto.GemUpdateRequestDto;
import com.gemora_server.entity.Certificate;
import com.gemora_server.entity.Gem;
import com.gemora_server.entity.GemImage;
import com.gemora_server.entity.User;
import com.gemora_server.enums.GemStatus;
import com.gemora_server.enums.ListingType;
import com.gemora_server.repo.CertificateRepo;
import com.gemora_server.repo.GemImageRepo;
import com.gemora_server.repo.GemRepo;
import com.gemora_server.repo.UserRepo;
import com.gemora_server.service.FileStorageService;
import com.gemora_server.service.GemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GemServiceImpl implements GemService {

    private final GemRepo gemRepo;
    private final UserRepo userRepo;
    private final GemImageRepo gemImageRepo;
    private final CertificateRepo certificateRepo;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public GemDto createGem(Long sellerId, GemCreateRequest request, List<MultipartFile> images, MultipartFile certificateFile) {
        User seller = userRepo.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        Gem gem = Gem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .carat(request.getCarat())
                .origin(request.getOrigin())
                .price(request.getPrice())
                .listingType(request.getListingType() == null ? ListingType.SALE : request.getListingType())
                .seller(seller)
                .status(GemStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Gem savedGem = gemRepo.save(gem);

        // ✅ Save images (if provided)
        if (images != null && !images.isEmpty()) {
            if (savedGem.getImages() == null) savedGem.setImages(new ArrayList<>());

            for (MultipartFile img : images) {
                String fileName = fileStorageService.storeGemImage(img);
                String url = "/uploads/gems/" + fileName;
                GemImage gemImage = GemImage.builder()
                        .fileName(fileName)
                        .fileUrl(url)
                        .gem(savedGem)
                        .build();
                gemImageRepo.save(gemImage);
                savedGem.getImages().add(gemImage);
            }
        }

        // ✅ Save certificate if provided
        if (certificateFile != null && !certificateFile.isEmpty()) {
            String fileName = fileStorageService.storeCertificateFile(certificateFile);
            String url = "/uploads/certificates/" + fileName;

            LocalDate parsedIssueDate = null;
            try {
                if (request.getIssueDate() != null) {
                    parsedIssueDate = LocalDate.parse(request.getIssueDate());
                }
            } catch (Exception ignored) {}

            Certificate cert = Certificate.builder()
                    .certificateNumber(request.getCertificateNumber())
                    .issuingAuthority(request.getIssuingAuthority())
                    .issueDate(parsedIssueDate)
                    .fileName(fileName)
                    .fileUrl(url)
                    .verified(false)
                    .uploadedAt(LocalDateTime.now())
                    .gem(savedGem)
                    .build();

            certificateRepo.save(cert);

            if (savedGem.getCertificates() == null) savedGem.setCertificates(new ArrayList<>());
            savedGem.getCertificates().add(cert);
        }

        return mapToDto(savedGem);
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
        String url = "/uploads/certificates/" + fileName;
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
        if (gem.getCertificates() == null) gem.setCertificates(new ArrayList<>());
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


    @Override
    @Transactional
    public void deleteGemAsAdmin(Long gemId, String adminUsername) {
        Gem gem = gemRepo.findById(gemId)
                .orElseThrow(() -> new RuntimeException("Gem not found"));

        // TODO: store audit log of who deleted the gem (future enhancement)

        //  Delete gem images
        if (gem.getImages() != null && !gem.getImages().isEmpty()) {
            gem.getImages().forEach(img -> fileStorageService.deleteFile(img.getFileName(), false));
            gemImageRepo.deleteAll(gem.getImages());
        }

        //  Delete certificates and their files
        if (gem.getCertificates() != null && !gem.getCertificates().isEmpty()) {
            gem.getCertificates().forEach(cert -> fileStorageService.deleteFile(cert.getFileName(), true));
            certificateRepo.deleteAll(gem.getCertificates());
        }

        //  Finally delete gem record
        gemRepo.delete(gem);
    }

    @Override
    @Transactional
    public GemDto updateGem(Long gemId, Long sellerId, GemUpdateRequestDto req,
                            List<MultipartFile> newImages, MultipartFile certificateFile) {

        Gem gem = gemRepo.findById(gemId).orElseThrow(() -> new RuntimeException("Gem not found"));
        if (!gem.getSeller().getId().equals(sellerId)) {
            throw new RuntimeException("Not authorized to update this gem");
        }

        // Allow edits only if PENDING or REJECTED
        if (!(gem.getStatus() == GemStatus.PENDING || gem.getStatus() == GemStatus.REJECTED)) {
            throw new RuntimeException("Cannot edit gem after approval");
        }

        // Update basic details if provided
        if (req.getName() != null) gem.setName(req.getName());
        if (req.getDescription() != null) gem.setDescription(req.getDescription());
        if (req.getCategory() != null) gem.setCategory(req.getCategory());
        if (req.getCarat() != null) gem.setCarat(req.getCarat());
        if (req.getOrigin() != null) gem.setOrigin(req.getOrigin());
        if (req.getPrice() != null) gem.setPrice(req.getPrice());
        if (req.getListingType() != null) gem.setListingType(req.getListingType());

        // Remove selected images
        if (req.getRemoveImageIds() != null) {
            List<GemImage> imagesToRemove = gem.getImages().stream()
                    .filter(img -> req.getRemoveImageIds().contains(img.getId()))
                    .collect(Collectors.toList());

            imagesToRemove.forEach(img -> fileStorageService.deleteFile(img.getFileName(), false));
            gemImageRepo.deleteAll(imagesToRemove);
            gem.getImages().removeAll(imagesToRemove);
        }

        // Add new images if provided
        if (newImages != null) {
            for (MultipartFile img : newImages) {
                String fileName = fileStorageService.storeGemImage(img);
                GemImage newImg = GemImage.builder()
                        .fileName(fileName)
                        .fileUrl("/uploads/gems/" + fileName)
                        .gem(gem)
                        .build();
                gemImageRepo.save(newImg);
                gem.getImages().add(newImg);
            }
        }

        // Replace or add certificate
        if (certificateFile != null) {
            certificateRepo.deleteAll(gem.getCertificates());
            String fileName = fileStorageService.storeCertificateFile(certificateFile);
            Certificate cert = Certificate.builder()
                    .certificateNumber(req.getCertificateNumber())
                    .issuingAuthority(req.getIssuingAuthority())
                    .issueDate(req.getIssueDate() != null ? LocalDate.parse(req.getIssueDate()) : null)
                    .fileName(fileName)
                    .fileUrl("/uploads/certificates/" + fileName)
                    .verified(false)
                    .uploadedAt(LocalDateTime.now())
                    .gem(gem)
                    .build();
            certificateRepo.save(cert);
            gem.setCertificates(List.of(cert));
        }

        // Reset status to PENDING so admin re-approves
        gem.setStatus(GemStatus.PENDING);
        gem.setUpdatedAt(LocalDateTime.now());

        gemRepo.save(gem);
        return mapToDto(gem);
    }


    // helper mapping
    private GemDto mapToDto(Gem gem) {
        List<String> imageUrls = gem.getImages() == null ? Collections.emptyList() :
                gem.getImages().stream().map(GemImage::getFileUrl).collect(Collectors.toList());
        List<CertificateDto> certs = gem.getCertificates() == null ? Collections.emptyList() :
                gem.getCertificates().stream().map(c -> CertificateDto.builder()
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

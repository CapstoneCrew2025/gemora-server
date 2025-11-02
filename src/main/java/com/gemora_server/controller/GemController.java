package com.gemora_server.controller;

import com.gemora_server.dto.GemCreateRequest;
import com.gemora_server.dto.GemDto;
import com.gemora_server.service.GemService;
import com.gemora_server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/gems")
@RequiredArgsConstructor
public class GemController {

    private final GemService gemService;
    private final JwtUtil jwtUtil;

    //  Create new gem listing
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<GemDto> createGem(
            @RequestHeader("Authorization") String token,
            @ModelAttribute GemCreateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "certificateFile", required = false) MultipartFile certificateFile) {

        Long userId = jwtUtil.extractUserId(token);
        GemDto saved = gemService.createGem(userId, request, images, certificateFile);
        return ResponseEntity.ok(saved);
    }


    //  Get all approved gems (for marketplace view)
    @GetMapping("/approved")
    public ResponseEntity<List<GemDto>> getApprovedGems() {
        List<GemDto> gems = gemService.getApprovedGems();
        return ResponseEntity.ok(gems);
    }


    //  Get all gems created by logged-in user
    @GetMapping("/mine")
    public ResponseEntity<List<GemDto>> getMyGems(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractUserId(token);
        List<GemDto> gems = gemService.getMyGems(userId);
        return ResponseEntity.ok(gems);
    }


    //  Upload a certificate for an existing gem
    @PostMapping(value = "/{gemId}/certificate", consumes = {"multipart/form-data"})
    public ResponseEntity<GemDto> uploadCertificate(@RequestHeader("Authorization") String token, @PathVariable Long gemId, @RequestPart("file") MultipartFile certificateFile, @RequestParam(required = false) String certificateNumber, @RequestParam(required = false) String issuingAuthority, @RequestParam(required = false) String issueDate) {
        // userId not directly needed now, but kept for token validation
        jwtUtil.extractUserId(token);
        GemDto updated = gemService.uploadCertificate(gemId, certificateFile, certificateNumber, issuingAuthority, issueDate);
        return ResponseEntity.ok(updated);
    }


    //  Get gem by ID (for viewing details)
    @GetMapping("/{id}")
    public ResponseEntity<GemDto> getGem(@PathVariable Long id) {
        GemDto gem = gemService.getGem(id);
        return ResponseEntity.ok(gem);
    }


    //  Delete gem (only if owner)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGem(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        Long userId = jwtUtil.extractUserId(token);
        gemService.deleteGem(id, userId);
        return ResponseEntity.noContent().build();
    }


}

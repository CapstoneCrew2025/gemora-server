package com.gemora_server.controller;

import com.gemora_server.dto.GemDto;
import com.gemora_server.service.GemService;
import com.gemora_server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/gems")
@RequiredArgsConstructor
public class AdminGemController {

    private final GemService gemService;
    private final JwtUtil jwtUtil;

    // ✅ Get all pending gems for admin review
    @GetMapping("/pending")
    public ResponseEntity<List<GemDto>> getPendingGems() {
        List<GemDto> gems = gemService.getAllGemsByStatus("PENDING");
        return ResponseEntity.ok(gems);
    }

    // ✅ Approve gem
    @PutMapping("/{id}/approve")
    public ResponseEntity<GemDto> approveGem(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        String adminUsername = jwtUtil.extractUsername(token);
        GemDto approved = gemService.approveGem(id, adminUsername);
        return ResponseEntity.ok(approved);
    }

    // ✅ Reject gem
    @PutMapping("/{id}/reject")
    public ResponseEntity<GemDto> rejectGem(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestParam(required = false) String reason
    ) {
        String adminUsername = jwtUtil.extractUsername(token);
        GemDto rejected = gemService.rejectGem(id, adminUsername, reason);
        return ResponseEntity.ok(rejected);
    }

    // ✅ Verify or unverify a certificate
    @PutMapping("/certificate/{certificateId}/verify")
    public ResponseEntity<Void> verifyCertificate(
            @RequestHeader("Authorization") String token,
            @PathVariable Long certificateId,
            @RequestParam boolean verified
    ) {
        String adminUsername = jwtUtil.extractUsername(token);
        gemService.verifyCertificate(certificateId, verified, adminUsername);
        return ResponseEntity.noContent().build();
    }



}

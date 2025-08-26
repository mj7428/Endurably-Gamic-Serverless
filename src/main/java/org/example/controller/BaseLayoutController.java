package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.Entity.BaseLayout;
import org.example.Entity.Users;
import org.example.dto.BaseLayoutDto;
import org.example.dto.CreateBaseRequestDto;
import org.example.service.BaseLayoutService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/bases")
@RequiredArgsConstructor
public class BaseLayoutController {

    private final BaseLayoutService baseLayoutService;

    @GetMapping
    public ResponseEntity<Page<BaseLayoutDto>> getAllBaseLayouts(
            @RequestParam Optional<Integer> townhallLevel,
            Pageable pageable
    ) {
        Page<BaseLayoutDto> layoutDtos = baseLayoutService.findAll(townhallLevel, pageable)
                .map(this::convertToDto);
        return ResponseEntity.ok(layoutDtos);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> createBase(
            @Valid @RequestBody CreateBaseRequestDto requestDto
    ) {
        baseLayoutService.createNewBase(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Base Layout submitted for review.");
    }

    @GetMapping("/my-bases")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<BaseLayoutDto>> getMyBases(Pageable pageable) {
        Page<BaseLayoutDto> layoutDtos = baseLayoutService.findBasesByCurrentUser(pageable)
                .map(this::convertToDto);
        return ResponseEntity.ok(layoutDtos);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BaseLayoutDto>> getPendingBases(Pageable pageable) {
        Page<BaseLayoutDto> pendingBases = baseLayoutService.findAllPending(pageable)
                .map(this::convertToDto);
        return ResponseEntity.ok(pendingBases);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> approveBase(@PathVariable String id) { // Changed to String
        baseLayoutService.approveBase(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> rejectBase(@PathVariable String id) { // Changed to String
        baseLayoutService.rejectBase(id);
        return ResponseEntity.ok().build();
    }

    private BaseLayoutDto convertToDto(BaseLayout baseLayout) {

        return BaseLayoutDto.builder()
                .id(baseLayout.getId())
                .title(baseLayout.getTitle())
                .townhallLevel(baseLayout.getTownhallLevel())
                .baseLink(baseLayout.getBaseLink())
                .imageUrl(baseLayout.getImageUrl())
                .submittedByUsername(baseLayout.getSubmittedBy().getName()) // Use the safe username variable
                .status(baseLayout.getStatus().name())
                .build();
    }
}

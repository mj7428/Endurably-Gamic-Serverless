package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.Entity.BaseLayout;
import org.example.Entity.Users;
import org.example.repository.BaseLayoutRepository;
import org.example.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.example.dto.CreateBaseRequestDto;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BaseLayoutService {

    private final BaseLayoutRepository baseLayoutRepository;
    private final UserRepository userRepository;

    @Cacheable("baseLayouts")
    public Page<BaseLayout> findAll(Optional<Integer> townhallLevel, Pageable pageable) {
        if (townhallLevel.isPresent()) {
            return baseLayoutRepository.findAllByStatusAndTownhallLevel(BaseLayout.BaseStatus.APPROVED, townhallLevel.get(), pageable);
        } else {
            return baseLayoutRepository.findAllByStatus(BaseLayout.BaseStatus.APPROVED, pageable);
        }
    }

    @CacheEvict(value = {"baseLayouts", "pendingBases"}, allEntries = true)
    public void createNewBase(CreateBaseRequestDto requestDto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Users currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database"));

        BaseLayout newLayout = BaseLayout.builder()
                .title(requestDto.getTitle())
                .townhallLevel(requestDto.getTownhallLevel())
                .baseLink(requestDto.getBaseLink())
                .imageUrl(requestDto.getImageUrl())
                .submittedBy(currentUser)
                .build();

        baseLayoutRepository.save(newLayout);
    }

    public Page<BaseLayout> findBasesByCurrentUser(Pageable pageable) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Users currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database"));
        return baseLayoutRepository.findAllBySubmittedBy(currentUser, pageable);
    }

    @Cacheable("pendingBases")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<BaseLayout> findAllPending(Pageable pageable) {
        return baseLayoutRepository.findAllByStatus(BaseLayout.BaseStatus.PENDING, pageable);
    }

    @CacheEvict(value = {"baseLayouts", "pendingBases"}, allEntries = true)
    @PreAuthorize("hasRole('ADMIN')")
    public void approveBase(String baseId) { // Changed to String
        BaseLayout base = baseLayoutRepository.findById(baseId)
                .orElseThrow(() -> new IllegalStateException("Base layout not found with ID: " + baseId));
        base.setStatus(BaseLayout.BaseStatus.APPROVED);
        baseLayoutRepository.save(base);
    }

    @CacheEvict(value = {"baseLayouts", "pendingBases"}, allEntries = true)
    @PreAuthorize("hasRole('ADMIN')")
    public void rejectBase(String baseId) { // Changed to String
        BaseLayout base = baseLayoutRepository.findById(baseId)
                .orElseThrow(() -> new IllegalStateException("Base layout not found with ID: " + baseId));
        base.setStatus(BaseLayout.BaseStatus.REJECTED);
        baseLayoutRepository.save(base);
    }
}

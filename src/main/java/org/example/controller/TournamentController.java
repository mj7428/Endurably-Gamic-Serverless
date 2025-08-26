package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.Entity.Tournament;
import org.example.dto.*;
import org.example.service.TournamentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tournaments")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TournamentDetailDto> createTournament(@Valid @RequestBody CreateTournamentRequestDto requestDto) {
        Tournament newTournament = tournamentService.createTournament(requestDto);
        // Fetch the newly created tournament to get the full DTO representation
        return tournamentService.findTournamentDetailsById(newTournament.getId(), null)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.CREATED))
                .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @PostMapping("/{id}/start")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> startTournament(@PathVariable String id) {
        try {
            tournamentService.startTournament(id);
            return ResponseEntity.ok("Tournament started successfully and first round matches have been generated.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * ✅ UPDATED ENDPOINT: Sets the winner for a specific match using its round and match number.
     */
    @PostMapping("/{tournamentId}/rounds/{roundNumber}/matches/{matchNumber}/winner")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> declareWinner(
            @PathVariable String tournamentId,
            @PathVariable int roundNumber,
            @PathVariable int matchNumber,
            @Valid @RequestBody DeclareWinnerRequestDto requestDto
    ) {
        try {
            tournamentService.declareMatchWinner(tournamentId, roundNumber, matchNumber, requestDto.getWinnerTeamId());
            return ResponseEntity.ok("Winner declared successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentDetailDto> getTournamentById(
            @PathVariable String id,
            Authentication authentication
    ) {
        UserDetails currentUser = null;
        if (authentication != null && authentication.isAuthenticated()) {
            currentUser = (UserDetails) authentication.getPrincipal();
        }

        return tournamentService.findTournamentDetailsById(id, currentUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<TournamentListDto>> getAllTournaments(Pageable pageable) {
        Page<TournamentListDto> tournaments = tournamentService.findAllTournaments(pageable);
        return ResponseEntity.ok(tournaments);
    }

    @GetMapping("/{tournamentId}/registrations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TeamRegistrationResponseDto>> getRegistrations(@PathVariable String tournamentId) {
        List<TeamRegistrationResponseDto> registrations = tournamentService.getRegistrationsForTournament(tournamentId);
        return ResponseEntity.ok(registrations);
    }

    // Note: All mapping logic has been moved to the TournamentService to keep the controller clean.
}

package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.Entity.TeamRegistration;
import org.example.dto.FieldValueResponseDto;
import org.example.dto.PlayerSubmissionResponseDto;
import org.example.dto.TeamRegistrationRequestDto;
import org.example.dto.TeamRegistrationResponseDto;
import org.example.service.TeamRegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tournaments/{tournamentId}/register")
@RequiredArgsConstructor
public class TeamRegistrationController {

    private final TeamRegistrationService registrationService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TeamRegistrationResponseDto> registerTeam(
            @PathVariable String tournamentId, // Changed from Long to String
            @Valid @RequestBody TeamRegistrationRequestDto requestDto) {

        TeamRegistration newRegistration = registrationService.registerTeamForTournament(tournamentId, requestDto);
        return new ResponseEntity<>(mapToDto(newRegistration), HttpStatus.CREATED);
    }

    private TeamRegistrationResponseDto mapToDto(TeamRegistration registration) {
        return TeamRegistrationResponseDto.builder()
                .registrationId(registration.getId()) // ID is now a String
                .submittedByEmail(registration.getSubmittedBy().getEmail())
                .teamFields(registration.getTeamFieldValues().stream()
                        .map(fv -> new FieldValueResponseDto(fv.getFieldDefinition().getFieldName(), fv.getValue()))
                        .collect(Collectors.toList()))
                .playerSubmissions(registration.getPlayerSubmissions().stream()
                        .map(ps -> new PlayerSubmissionResponseDto(
//                                ps.getId(), // ID is now a String
                                ps.getFieldValues().stream()
                                        .map(fv -> new FieldValueResponseDto(fv.getFieldDefinition().getFieldName(), fv.getValue()))
                                        .collect(Collectors.toList())
                        ))
                        .collect(Collectors.toList()))
                .build();
    }
}

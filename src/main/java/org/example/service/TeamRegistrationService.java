package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.Entity.*;
import org.example.dto.TeamRegistrationRequestDto;
import org.example.repository.TeamRegistrationRepository;
import org.example.repository.TournamentRepository;
import org.example.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamRegistrationService {

    private final TournamentRepository tournamentRepository;
    private final TeamRegistrationRepository teamRegistrationRepository;
    private final UserRepository userRepository;

    @Transactional
    public TeamRegistration registerTeamForTournament(String tournamentId, TeamRegistrationRequestDto requestDto) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalStateException("Tournament not found with ID: " + tournamentId));

        if (tournament.getStartDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Registration for this tournament has closed.");
        }

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Users currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

        List<TeamRegistration> existing = teamRegistrationRepository.findByTournamentIdAndSubmittedById(tournamentId, currentUser.getId());
        if (!existing.isEmpty()) {
            throw new IllegalStateException("You have already registered for this tournament.");
        }

        TeamRegistration registration = TeamRegistration.builder()
                .tournament(tournament)
                .submittedBy(currentUser)
                .build();

        Map<String, RegistrationField> fieldMap = tournament.getRequiredFields().stream()
                .collect(Collectors.toMap(RegistrationField::getFieldName, Function.identity()));

        // Process team-level fields
        requestDto.getTeamFields().forEach(teamFieldDto -> {
            RegistrationField fieldDef = fieldMap.get(teamFieldDto.getFieldName());
            if (fieldDef == null) {
                throw new IllegalStateException("Invalid registration field provided: " + teamFieldDto.getFieldName());
            }
            // ✅ FIX: Add directly to the list
            registration.getTeamFieldValues().add(FieldValue.builder()
                    .fieldDefinition(fieldDef)
                    .value(teamFieldDto.getValue())
                    .build());
        });

        // Process player-level fields
        requestDto.getPlayerSubmissions().forEach(playerDto -> {
            PlayerSubmission playerSubmission = new PlayerSubmission();
            playerDto.getFieldValues().forEach(valueDto -> {
                RegistrationField fieldDef = fieldMap.get(valueDto.getFieldName());
                if (fieldDef == null) {
                    throw new IllegalStateException("Invalid registration field provided: " + valueDto.getFieldName());
                }
                // ✅ FIX: Add directly to the list
                playerSubmission.getFieldValues().add(FieldValue.builder()
                        .fieldDefinition(fieldDef)
                        .value(valueDto.getValue())
                        .build());
            });
            // ✅ FIX: Add directly to the list
            registration.getPlayerSubmissions().add(playerSubmission);
        });

        return teamRegistrationRepository.save(registration);
    }
}

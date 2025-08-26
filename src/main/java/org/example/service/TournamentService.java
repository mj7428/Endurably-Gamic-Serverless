package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.Entity.*;
import org.example.dto.*;
import org.example.repository.TeamRegistrationRepository;
import org.example.repository.TournamentRepository;
import org.example.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final TeamRegistrationRepository teamRegistrationRepository;
    private final UserRepository userRepository;
    // MatchRepository is no longer needed

    @Transactional
    public Tournament createTournament(CreateTournamentRequestDto requestDto) {
        List<RegistrationField> fields = requestDto.getRequiredFields().stream()
                .map(fieldDto -> RegistrationField.builder()
                        .fieldName(fieldDto.getFieldName())
                        .fieldType(fieldDto.getFieldType())
                        .isRequired(fieldDto.isRequired())
                        .build())
                .collect(Collectors.toList());

        Tournament tournament = Tournament.builder()
                .name(requestDto.getName())
                .gameName(requestDto.getGameName())
                .startDate(requestDto.getStartDate())
                .prizePool(requestDto.getPrizePool())
                .teamSize(requestDto.getTeamSize())
                .rules(requestDto.getRules())
                .requiredFields(fields)
                .build();

        return tournamentRepository.save(tournament);
    }

    @Transactional(readOnly = true)
    public Page<TournamentListDto> findAllTournaments(Pageable pageable) {
        return tournamentRepository.findAll(pageable)
                .map(this::mapToTournamentListDto);
    }

    @Transactional(readOnly = true)
    public List<TeamRegistrationResponseDto> getRegistrationsForTournament(String tournamentId) {
        return teamRegistrationRepository.findByTournamentId(tournamentId).stream()
                .map(this::mapToTeamRegistrationDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<TournamentDetailDto> findTournamentDetailsById(String id, UserDetails currentUserDetails) {
        Optional<Tournament> tournamentOpt = tournamentRepository.findById(id);
        if (tournamentOpt.isEmpty()) {
            return Optional.empty();
        }
        Tournament tournament = tournamentOpt.get();
        TournamentDetailDto dto = mapToDetailDto(tournament);

        if (currentUserDetails != null) {
            userRepository.findByEmail(currentUserDetails.getUsername())
                    .ifPresent(currentUser -> {
                        List<TeamRegistration> registrations = teamRegistrationRepository.findByTournamentIdAndSubmittedById(id, currentUser.getId());
                        if (!registrations.isEmpty()) {
                            dto.setUserRegistration(mapToTeamRegistrationDto(registrations.get(0)));
                        }
                    });
        }

        return Optional.of(dto);
    }

    @Transactional
    public void startTournament(String tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalStateException("Tournament not found with ID: " + tournamentId));

        if (tournament.getStatus() != Tournament.TournamentStatus.REGISTRATION_OPEN) {
            throw new IllegalStateException("Tournament has already started or is completed.");
        }

        // ✅ FIX: Fetch the registrations directly from the repository
        List<TeamRegistration> registeredTeams = teamRegistrationRepository.findByTournamentId(tournamentId);

        if (registeredTeams.size() < 2) {
            throw new IllegalStateException("Cannot start a tournament with fewer than 2 teams.");
        }

        Collections.shuffle(registeredTeams);
        List<Match> firstRoundMatches = new ArrayList<>();
        int matchNumber = 1;

        for (int i = 0; i < registeredTeams.size(); i += 2) {
            TeamRegistration teamA = registeredTeams.get(i);
            TeamRegistration teamB = (i + 1 < registeredTeams.size()) ? registeredTeams.get(i + 1) : null;

            Match.MatchBuilder matchBuilder = Match.builder()
                    .teamAId(teamA.getId())
                    .teamBId(teamB != null ? teamB.getId() : null)
                    .roundNumber(1)
                    .matchNumber(matchNumber++);

            if (teamB == null) {
                matchBuilder.winnerId(teamA.getId());
                matchBuilder.status(Match.MatchStatus.COMPLETED);
            }
            firstRoundMatches.add(matchBuilder.build());
        }

        tournament.setMatches(firstRoundMatches);
        tournament.setStatus(Tournament.TournamentStatus.IN_PROGRESS);
        tournamentRepository.save(tournament);
    }

    @Transactional
    public void declareMatchWinner(String tournamentId, int roundNumber, int matchNumber, String winnerTeamId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalStateException("Tournament not found"));

        Match matchToUpdate = tournament.getMatches().stream()
                .filter(m -> m.getRoundNumber() == roundNumber && m.getMatchNumber() == matchNumber)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Match not found in the specified round"));

        if (matchToUpdate.getStatus() == Match.MatchStatus.COMPLETED) {
            throw new IllegalStateException("A winner has already been declared for this match.");
        }

        // Validate that the winner is one of the participants
        if (!winnerTeamId.equals(matchToUpdate.getTeamAId()) && !winnerTeamId.equals(matchToUpdate.getTeamBId())) {
            throw new IllegalStateException("The declared winner is not a participant in this match.");
        }

        matchToUpdate.setWinnerId(winnerTeamId);
        matchToUpdate.setStatus(Match.MatchStatus.COMPLETED);

        checkForNextRound(tournament);

        tournamentRepository.save(tournament);
    }

    private void checkForNextRound(Tournament tournament) {
        int currentRound = getCurrentRound(tournament);
        List<Match> currentRoundMatches = tournament.getMatches().stream()
                .filter(m -> m.getRoundNumber() == currentRound)
                .collect(Collectors.toList());

        boolean isRoundComplete = currentRoundMatches.stream().allMatch(m -> m.getStatus() == Match.MatchStatus.COMPLETED);

        if (isRoundComplete) {
            List<String> winnerIds = currentRoundMatches.stream().map(Match::getWinnerId).collect(Collectors.toList());
            if (winnerIds.size() == 1) {
                tournament.setStatus(Tournament.TournamentStatus.COMPLETED);
            } else {
                generateNextRoundMatches(tournament, winnerIds, currentRound + 1);
            }
        }
    }

    private void generateNextRoundMatches(Tournament tournament, List<String> teamIds, int roundNumber) {
        List<TeamRegistration> teams = (List<TeamRegistration>) teamRegistrationRepository.findAllById(teamIds);
        Collections.shuffle(teams);
        int matchNumber = 1;

        for (int i = 0; i < teams.size(); i += 2) {
            TeamRegistration teamA = teams.get(i);
            TeamRegistration teamB = (i + 1 < teams.size()) ? teams.get(i + 1) : null;

            Match.MatchBuilder matchBuilder = Match.builder()
                    .teamAId(teamA.getId())
                    .teamBId(teamB != null ? teamB.getId() : null)
                    .roundNumber(roundNumber)
                    .matchNumber(matchNumber++);

            if (teamB == null) {
                matchBuilder.winnerId(teamA.getId());
                matchBuilder.status(Match.MatchStatus.COMPLETED);
            }
            tournament.getMatches().add(matchBuilder.build());
        }
    }

    private int getCurrentRound(Tournament tournament) {
        return tournament.getMatches().stream().mapToInt(Match::getRoundNumber).max().orElse(0);
    }

    // --- Private Mapping Methods ---

    private TournamentDetailDto mapToDetailDto(Tournament tournament) {
        // ✅ FIX: Explicitly fetch registrations to avoid lazy loading issues.
        List<TeamRegistration> registrations = teamRegistrationRepository.findByTournamentId(tournament.getId());
        Map<String, TeamRegistrationResponseDto> registrationMap = registrations.stream()
                .collect(Collectors.toMap(TeamRegistration::getId, this::mapToTeamRegistrationDto));

        return TournamentDetailDto.builder()
                .id(tournament.getId())
                .name(tournament.getName())
                .gameName(tournament.getGameName())
                .startDate(tournament.getStartDate())
                .prizePool(tournament.getPrizePool())
                .teamSize(tournament.getTeamSize())
                .rules(tournament.getRules())
                .status(tournament.getStatus().name())
                .requiredFields(tournament.getRequiredFields().stream()
                        .map(this::mapToRegistrationFieldDto)
                        .collect(Collectors.toList()))
                .matches(tournament.getMatches().stream()
                        .map(match -> mapToMatchDto(match, registrationMap))
                        .collect(Collectors.toList()))
                .build();
    }

    private MatchDto mapToMatchDto(Match match, Map<String, TeamRegistrationResponseDto> registrationMap) {
        return MatchDto.builder()
                .id(null) // Embedded matches don't have their own top-level ID
                .roundNumber(match.getRoundNumber())
                .matchNumber(match.getMatchNumber())
                .status(match.getStatus().name())
                .teamA(registrationMap.get(match.getTeamAId()))
                .teamB(registrationMap.get(match.getTeamBId()))
                .winner(registrationMap.get(match.getWinnerId()))
                .build();
    }

    private RegistrationFieldDto mapToRegistrationFieldDto(RegistrationField field) {
        return RegistrationFieldDto.builder()
                .fieldName(field.getFieldName())
                .fieldType(field.getFieldType())
                .isRequired(field.isRequired())
                .build();
    }

    private TeamRegistrationResponseDto mapToTeamRegistrationDto(TeamRegistration registration) {
        return TeamRegistrationResponseDto.builder()
                .registrationId(registration.getId())
                .submittedByEmail(registration.getSubmittedBy().getEmail())
                .teamFields(registration.getTeamFieldValues().stream()
                        .map(this::mapToFieldValueResponseDto)
                        .collect(Collectors.toList()))
                .playerSubmissions(registration.getPlayerSubmissions().stream()
                        .map(this::mapToPlayerSubmissionDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private PlayerSubmissionResponseDto mapToPlayerSubmissionDto(PlayerSubmission submission) {
        return PlayerSubmissionResponseDto.builder()
                .fieldValues(submission.getFieldValues().stream()
                        .map(this::mapToFieldValueResponseDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private FieldValueResponseDto mapToFieldValueResponseDto(FieldValue fieldValue) {
        return FieldValueResponseDto.builder()
                .fieldName(fieldValue.getFieldDefinition().getFieldName())
                .value(fieldValue.getValue())
                .build();
    }

    private TournamentListDto mapToTournamentListDto(Tournament tournament) {
        return TournamentListDto.builder()
                .id(tournament.getId())
                .name(tournament.getName())
                .gameName(tournament.getGameName())
                .startDate(tournament.getStartDate())
                .prizePool(tournament.getPrizePool())
                .teamSize(tournament.getTeamSize())
                .status(tournament.getStatus().name())
                .build();
    }
}

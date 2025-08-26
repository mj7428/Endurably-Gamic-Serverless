package org.example.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MatchDto {
    private String id; // Changed from Long to String
    private int roundNumber;
    private int matchNumber;
    private TeamRegistrationResponseDto teamA;
    private TeamRegistrationResponseDto teamB; // Can be null for a bye
    private TeamRegistrationResponseDto winner; // Can be null
    private String status;
}

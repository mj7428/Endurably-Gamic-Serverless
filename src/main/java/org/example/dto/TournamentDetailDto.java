package org.example.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TournamentDetailDto {
    private String id; // Changed from Long to String
    private String name;
    private String gameName;
    private LocalDateTime startDate;
    private String prizePool;
    private int teamSize;
    private String rules;
    private List<RegistrationFieldDto> requiredFields;
    private String status;
    private List<MatchDto> matches;
    private TeamRegistrationResponseDto userRegistration;
}

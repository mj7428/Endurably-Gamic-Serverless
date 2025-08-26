package org.example.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeclareWinnerRequestDto {

    @NotNull(message = "Winner team ID cannot be null")
    private String winnerTeamId; // Changed from Long to String
}

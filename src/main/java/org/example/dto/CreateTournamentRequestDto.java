package org.example.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CreateTournamentRequestDto {

    @NotBlank(message = "Tournament name is mandatory")
    private String name;

    @NotBlank(message = "Game name is mandatory")
    private String gameName;

    @NotNull(message = "Start date is mandatory")
    @Future(message = "Start date must be in the future")
    private LocalDateTime startDate;

    private String prizePool;

    @Min(value = 1, message = "Team size must be at least 1")
    private int teamSize;

    private String rules;

    @NotNull(message = "Required fields list cannot be null")
    @Valid
    private List<RegistrationFieldDto> requiredFields;
}
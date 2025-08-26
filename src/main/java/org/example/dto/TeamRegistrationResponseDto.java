package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamRegistrationResponseDto {
    private String registrationId; // Changed from Long to String
    private String submittedByEmail;
    private List<FieldValueResponseDto> teamFields;
    private List<PlayerSubmissionResponseDto> playerSubmissions;
}

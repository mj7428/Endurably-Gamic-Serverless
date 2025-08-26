package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TournamentListDto {
    private String id; // Changed from Long to String
    private String name;
    private String gameName;
    private LocalDateTime startDate;
    private String prizePool;
    private int teamSize;
    private String status;
}

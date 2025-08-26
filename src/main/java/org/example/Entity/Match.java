package org.example.Entity;

import lombok.*;

// This is now a simple data class, not a database document.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Match {

    // The ID is removed as it's not a top-level document.

    // We store the IDs of the teams directly as Strings.
    private String teamAId;
    private String teamBId;
    private String winnerId;

    private int roundNumber;
    private int matchNumber;

    @Builder.Default
    private MatchStatus status = MatchStatus.PENDING;

    public enum MatchStatus {
        PENDING,
        COMPLETED
    }
}

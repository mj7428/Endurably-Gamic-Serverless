package org.example.Entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tournament")
public class Tournament {

    @Id
    private String id;
    private String name;
    private String gameName;
    private LocalDateTime startDate;
    private String prizePool;
    private int teamSize;
    private String rules;

    @Builder.Default
    private TournamentStatus status = TournamentStatus.REGISTRATION_OPEN;

    @Builder.Default
    private List<RegistrationField> requiredFields = new ArrayList<>();

    @DBRef(lazy = true)
    @Builder.Default
    private List<TeamRegistration> registrations = new ArrayList<>();

    // ✅ The list of matches is now directly embedded in the tournament document.
    @Builder.Default
    private List<Match> matches = new ArrayList<>();

    public enum TournamentStatus {
        REGISTRATION_OPEN,
        IN_PROGRESS,
        COMPLETED
    }
}

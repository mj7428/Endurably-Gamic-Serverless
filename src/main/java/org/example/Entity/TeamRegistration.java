package org.example.Entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "team_registrations")
public class TeamRegistration {

    @Id
    private String id;

    @DBRef(lazy = true)
    private Tournament tournament;

    @DBRef(lazy = true)
    private Users submittedBy;

    // These are now lists of embedded objects, not references.
    @Builder.Default
    private List<FieldValue> teamFieldValues = new ArrayList<>();

    @Builder.Default
    private List<PlayerSubmission> playerSubmissions = new ArrayList<>();

    // Helper methods are no longer needed for the new service logic.
}

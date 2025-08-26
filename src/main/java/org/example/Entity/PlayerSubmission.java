package org.example.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

// This is now a simple data class, not a database document.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerSubmission {

    // The ID is removed as it's not a top-level document.

    @Builder.Default
    private List<FieldValue> fieldValues = new ArrayList<>();

    // The back-reference to TeamRegistration is removed.
    // The helper method is no longer needed.
}

package org.example.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// This is now a simple data class, not a database document.
// All database annotations have been removed.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldValue {

    // The fieldDefinition is now stored as a direct copy, not a reference.
    private RegistrationField fieldDefinition;

    private String value;

    // The back-references to TeamRegistration and PlayerSubmission are removed.
}

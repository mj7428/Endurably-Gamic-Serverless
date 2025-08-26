package org.example.Entity;

import lombok.*;

// This is now a simple Plain Old Java Object (POJO), not a database entity.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationField {

    // The ID is no longer needed here, as it's part of the Tournament
    private String fieldName;
    private String fieldType;
    private boolean isRequired;

    // The @DBRef back to Tournament is removed because this object will live inside it.
}

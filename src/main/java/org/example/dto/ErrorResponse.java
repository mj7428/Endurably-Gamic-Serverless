package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data // Generates getters, setters, toString(), etc.
@Builder // ✅ Creates the .builder() method
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    @Builder.Default // Sets a default value when using the builder
    private LocalDateTime timestamp = LocalDateTime.now();

    private int status;
    private String error;
    private List<String> messages;
}
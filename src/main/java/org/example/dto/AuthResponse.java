package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for sending a JWT back to the client upon successful authentication.
 */
@Data // Automatically generates getters, setters, toString(), equals(), and hashCode()
@Builder // Implements the builder pattern for easy object creation
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
}
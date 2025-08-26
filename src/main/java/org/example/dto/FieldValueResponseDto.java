package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldValueResponseDto {
    private String fieldName; // The question (e.g., "Clan Tag")
    private String value;     // The answer (e.g., "#ABC123XYZ")
}
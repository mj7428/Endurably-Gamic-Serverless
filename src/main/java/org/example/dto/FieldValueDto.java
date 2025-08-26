package org.example.dto;

import jakarta.validation.constraints.NotBlank; // Changed from NotEmpty
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldValueDto {

    @NotBlank(message = "Field name is mandatory")
    private String fieldName;

    @NotBlank(message = "Value is mandatory")
    private String value;
}

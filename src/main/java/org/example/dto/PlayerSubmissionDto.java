package org.example.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerSubmissionDto {

    @NotNull
    @NotEmpty
    @Valid // Ensure nested objects are validated
    private List<FieldValueDto> fieldValues;
}
package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationFieldDto {

    // The ID is no longer needed as this is an embedded object
    // private String id;

    @NotBlank(message = "Field name is mandatory")
    private String fieldName;

    @NotBlank(message = "Field type is mandatory")
    private String fieldType;

    private boolean isRequired;
}

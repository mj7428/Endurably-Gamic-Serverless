package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateBaseRequestDto {

    @NotBlank
    private String title;

    @NotNull
    private Integer townhallLevel;

    @NotBlank
    private String baseLink;

    private String imageUrl;
}
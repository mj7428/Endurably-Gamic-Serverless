package org.example.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BaseLayoutDto {

    private String id; // Changed from Long to String
    private String title;
    private Integer townhallLevel;
    private String baseLink;
    private String imageUrl;
    private String submittedByUsername;
    private String status;
}

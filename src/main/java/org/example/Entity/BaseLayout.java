package org.example.Entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field; // Import the @Field annotation

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "base_layout")
public class BaseLayout {

    @Id
    private String id;

    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotNull(message = "Townhall level is mandatory")
    @Field("townhall_level") // Maps this field to the 'townhall_level' column in MongoDB
    private Integer townhallLevel;

    @NotBlank(message = "Base link is mandatory")
    @Field("base_link") // Maps this field to the 'base_link' column
    private String baseLink;

    @Field("image_url") // Maps this field to the 'image_url' column
    private String imageUrl;

    @Builder.Default
    private BaseStatus status = BaseStatus.PENDING;

    @DocumentReference(lazy = false)
    @Field("user_id")
    private Users submittedBy;

    public enum BaseStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}

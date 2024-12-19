package com.dcode.product_service.dtoRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class CollectionRequest {
    @NotBlank(message = "Collection ID is required.")
    private String collectionId;

    @NotBlank(message = "Name is required.")
    private String name;

    @NotBlank(message = "Title is required.")
    private String title;

    @NotBlank(message = "Description is required.")
    private String description;

    @NotBlank(message = "Image is required.")
    private String image;

    @NotBlank(message = "Hex is required.")
    private String hex;

    @NotNull(message = "Colors are required.")
    private Set<String> colors;

    @NotBlank(message = "Color family ID is required.")
    private String colorFamilyId;

    @NotBlank(message = "Room ID is required.")
    private String roomId;

    @NotBlank(message = "Collection type ID is required.")
    private String collectionTypeId;

    @NotBlank(message = "Relative collection ID is required.")
    private String relativeCollectionId;

}

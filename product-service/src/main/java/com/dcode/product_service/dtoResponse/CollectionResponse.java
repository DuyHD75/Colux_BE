package com.dcode.product_service.dtoResponse;

import com.dcode.product_service.entity.ColorFamily;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Set;
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CollectionResponse {
    @JsonProperty("id")
    private String collectionId;
    private String name;
    private String title;
    private String description;
    private String image;
    private String hex;
    private Set<ColorResponse> colors;
    private ColorFamilyResponse colorFamily;
    private RoomResponse room;
    private RelativeCollectionResponse relativeCollection;
}

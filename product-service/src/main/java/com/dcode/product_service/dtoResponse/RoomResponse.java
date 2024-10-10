package com.dcode.product_service.dtoResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomResponse {
    @JsonProperty("id")
    private String roomId;
    private String roomType;
    private String hex;
    private String title;
    private String description;
    private String image;
    private String textUrl3D;
    private Set<CollectionResponse> collections;
}

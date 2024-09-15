package com.dcode.product_service.dtoResponse;

import com.dcode.product_service.entity.ColorFamily;
import lombok.Builder;
import lombok.Data;

import java.util.Set;
@Data
@Builder
public class CollectionResponse {
    private String name;
    private Set<ColorResponse> colors;
    private ColorFamilyResponse colorFamily;
    private RoomResponse room;
    private RelativeCollectionResponse relativeCollection;
}

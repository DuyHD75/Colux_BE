package com.dcode.product_service.dto;

import com.dcode.product_service.dtoResponse.VariantResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class BuildNameGHN extends CartDtoBase{
    private VariantResponse variantResponse;
}

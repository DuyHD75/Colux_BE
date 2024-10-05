package com.dcode.product_service.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PageResponse <T>{
    private List<T> content;
    private int totalPages;
    private long totalElements;
    private int size;
    private int number;
}

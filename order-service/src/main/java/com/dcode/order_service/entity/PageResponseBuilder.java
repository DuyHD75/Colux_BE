package com.dcode.order_service.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class PageResponseBuilder {
    public static <T> PageResponse<T> buildPageResponse(Page<T> page){
        return PageResponse.<T>builder()
                .content(page.getContent())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .size(page.getSize())
                .number(page.getNumber())
                .build();
    }

    public static <T> PageResponse<T> buildPageResponseFromList(List<T> list, Pageable pageable, long totalElements) {
        Page<T> page = new PageImpl<>(list, pageable, totalElements);
        return buildPageResponse(page);
    }
}

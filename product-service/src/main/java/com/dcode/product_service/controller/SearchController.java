package com.dcode.product_service.controller;

import com.dcode.product_service.service.impl.SearchServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products/search")
@AllArgsConstructor
@Slf4j
public class SearchController {

    private SearchServiceImpl searchService;

    @GetMapping
    public ResponseEntity<Map<String, List<?>>> search(@RequestParam String keyword) {
        Map<String, List<?>> results = searchService.searchByKeyword(keyword);
        return ResponseEntity.ok(results);
    }

}

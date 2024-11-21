package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.service.impl.SearchServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/searches")
@AllArgsConstructor
@Slf4j
public class SearchController {

    private SearchServiceImpl searchService;

    @GetMapping("/public")
    public ResponseEntity<Response> search(@RequestParam String keyword,
                                           HttpServletRequest request) {
        Map<String, List<?>> results = searchService.searchByKeyword(keyword);
        return ResponseEntity.ok().body(getResponse(request, Map.of("Results", results), "Search results fetched successfully!", OK));
    }



}

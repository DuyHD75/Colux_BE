package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.CollectionRequest;
import com.dcode.product_service.dtoResponse.CollectionResponse;
import com.dcode.product_service.service.impl.CollectionServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/products/collections")
@AllArgsConstructor
public class CollectionController {

    private final CollectionServiceImpl collectionService;

    @PostMapping
    public ResponseEntity<Response> createACollection(@RequestBody CollectionRequest coRe, HttpServletRequest request) {
        collectionService.createACollection(coRe);
        return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Collection created successfully!", HttpStatus.CREATED));
    }

    @GetMapping("{collectionId}")
    public ResponseEntity<Response> getACollection(@PathVariable("collectionId")String collectionId, HttpServletRequest request){
        CollectionResponse collectionResponse =  collectionService.getACollection(collectionId);
        return  ResponseEntity.ok().body(getResponse(request, Map.of("collection", collectionResponse), "Collection retrieve successfully!", OK));
    }
    @GetMapping
    public ResponseEntity<Response> getAllCollection(HttpServletRequest request){
        var collections = collectionService.getAllCollection();
        return ResponseEntity.ok().body(getResponse(request, Map.of("collections", collections), "Collections retrieve successfully!", OK));
    }

    @GetMapping("/no-colorFamily-room")
    public ResponseEntity<Response> getAllCollectionWithoutColorFamilyAndRoom(HttpServletRequest request){
        var collections = collectionService.getAllCollectionWithoutColorFamilyAndRoom();
        return ResponseEntity.ok().body(getResponse(request, Map.of("collections", collections), "Collection without color family and room retrieve successfully!", OK));
    }

    @GetMapping("/{collectionId}/colors")
    public ResponseEntity<Response> getColorByCollection(@PathVariable("collectionId")String collectionId,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         HttpServletRequest request){
        Pageable pageable = PageRequest.of(page, size);
        var colors = collectionService.getColorByCollection(collectionId, pageable);
        return ResponseEntity.ok().body(getResponse(request, Map.of("colors", colors), "Retrieve color from collection successfully!", OK));
    }
    private URI getUri() {
        return URI.create("");
    }

}

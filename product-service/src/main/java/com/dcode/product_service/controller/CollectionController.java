package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.CollectionRequest;
import com.dcode.product_service.dtoResponse.CollectionResponse;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.service.impl.CollectionServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.dcode.product_service.utils.RequestUtils.getErrorResponse;
import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/collections")
@AllArgsConstructor
public class CollectionController {

    private final CollectionServiceImpl collectionService;

    @PreAuthorize("hasRole('EMPLOYEE') and hasAuthority('product:create')")
    @PostMapping
    public ResponseEntity<Response> createCollections(@RequestBody List<CollectionRequest> coRe, HttpServletRequest request, HttpServletResponse response) {
        try {
            collectionService.createCollections(coRe);
            return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Collection created successfully!", HttpStatus.CREATED));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }

    }

    @GetMapping("/public/collectionId/{collectionId}")
    public ResponseEntity<Response> getACollection(@PathVariable("collectionId")String collectionId, HttpServletRequest request, HttpServletResponse response){
        try {
            CollectionResponse collectionResponse =  collectionService.getACollection(collectionId);
            return  ResponseEntity.ok().body(getResponse(request, Map.of("collection", collectionResponse), "Collection retrieve successfully!", OK));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }

    }
    @GetMapping("/public")
    public ResponseEntity<Response> getAllCollection(HttpServletRequest request, HttpServletResponse response){
        try {
            var collections = collectionService.getAllCollection();
            return ResponseEntity.ok().body(getResponse(request, Map.of("collections", collections), "Collections retrieve successfully!", OK));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }

    }

    @GetMapping("/public/no-colorFamily-room")
    public ResponseEntity<Response> getAllCollectionWithoutColorFamilyAndRoom(HttpServletRequest request, HttpServletResponse response){
        try {
            var collections = collectionService.getAllCollectionWithoutColorFamilyAndRoom();
            return ResponseEntity.ok().body(getResponse(request, Map.of("collections", collections), "Collection without color family and room retrieve successfully!", OK));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/public/collectionId/{collectionId}/colors")
    public ResponseEntity<Response> getColorByCollection(@PathVariable("collectionId")String collectionId,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         HttpServletRequest request,
                                                         HttpServletResponse response){
        try {

            Pageable pageable = PageRequest.of(page, size);
            var colors = collectionService.getColorByCollection(collectionId, pageable);
            return ResponseEntity.ok().body(getResponse(request, Map.of("colors", colors), "Retrieve color from collection successfully!", OK));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }
    private URI getUri() {
        return URI.create("");
    }

}

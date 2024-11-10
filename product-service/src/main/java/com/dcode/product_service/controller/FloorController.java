package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.FloorRequest;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.service.impl.FloorServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import static com.dcode.product_service.utils.RequestUtils.getErrorResponse;
import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("/api/v1/products/floors")
@AllArgsConstructor
@Slf4j
public class FloorController {
    private final FloorServiceImpl floorService;

    @PostMapping("{productId}")
    public ResponseEntity<Response> createAFloor(@PathVariable("productId") String productId, @RequestBody FloorRequest fRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
        floorService.createFloor(productId, fRequest);
        return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Floor created successfully!", CREATED));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            log.error("floor: ", exception);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping
    public ResponseEntity<Response> createFloors(@RequestBody Set<FloorRequest> floorRequests, HttpServletRequest request, HttpServletResponse response) {
        try {
            floorService.createFloors(floorRequests);
            return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Floor created successfully!", CREATED));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("{floorId}")
    public ResponseEntity<Response> getAFloor(@PathVariable("floorId") String floorId, HttpServletRequest request, HttpServletResponse response) {
        try{
        var floor = floorService.getAFloor(floorId);
        return ResponseEntity.ok().body(getResponse(request, Map.of("floor", floor), "Floor retrieve successfully!", OK));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    @PutMapping("{floorId}")
    public ResponseEntity<Response> updateAFloor(@PathVariable("floorId") String floorId, @RequestBody FloorRequest fRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
        floorService.updateAFloor(floorId, fRequest);
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Floor update successfully!", OK));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    @DeleteMapping("{floorId}")
    public ResponseEntity<Response> deleteAFloor(@PathVariable("floorId") String floorId, HttpServletRequest request, HttpServletResponse response) {
        try {
        floorService.deleteAFloor(floorId);
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Floor deleted successfully!", OK));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping
    public ResponseEntity<Response> getAllFloorPageable(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) {
        try {
        Pageable pageable = PageRequest.of(page, size);
        var floors = floorService.getAllFloorPageable(pageable);
        return ResponseEntity.ok().body(getResponse(request, Map.of("floors", floors), "Floor retrieve successfully!", OK));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    public URI getUri() {
        return URI.create("");
    }
}

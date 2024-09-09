package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.FloorRequest;
import com.dcode.product_service.service.impl.FloorServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/floors")
@AllArgsConstructor
public class FloorController {
    private final FloorServiceImpl floorService;

    @PostMapping("{productId}")
    public ResponseEntity<Response> createAFloor(@PathVariable("productId")String productId, @RequestBody FloorRequest fRequest, HttpServletRequest request){
        floorService.createAFloor(productId, fRequest.getFoamThickness(), fRequest.getAccessoryType(), fRequest.getPackagingMaterial(), fRequest.getNumberOfPiecesPerBox(), fRequest.getVariants());
        return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Floor created successfully!", CREATED));
    }
    @GetMapping("{floorId}")
    public ResponseEntity<Response> getAFloor(@PathVariable("floorId")String floorId, HttpServletRequest request){
        var floor = floorService.getAFloor(floorId);
        return ResponseEntity.ok().body(getResponse(request, Map.of("floor", floor), "Floor retrieve successfully!", OK));

    }
    @PutMapping("{floorId}")
    public ResponseEntity<Response> updateAFloor(@PathVariable("floorId")String floorId, @RequestBody FloorRequest fRequest, HttpServletRequest request){
        floorService.updateAFloor(floorId, fRequest.getFoamThickness(), fRequest.getAccessoryType(), fRequest.getPackagingMaterial(), fRequest.getNumberOfPiecesPerBox(), fRequest.getVariants());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Floor update successfully!", OK));
    }

    @DeleteMapping("{floorId}")
    public ResponseEntity<Response> deleteAFloor(@PathVariable("floorId")String floorId, HttpServletRequest request){
        floorService.deleteAFloor(floorId);
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Floor deleted successfully!", OK));
    }

    public URI getUri(){
        return URI.create("");
    }
}

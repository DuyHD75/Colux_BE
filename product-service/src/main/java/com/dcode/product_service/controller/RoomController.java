package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.RoomRequest;
import com.dcode.product_service.service.impl.RoomServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/products/rooms")
@AllArgsConstructor
public class RoomController {

    private final RoomServiceImpl roomService;

    @PostMapping
    public ResponseEntity<Response> createARoom(@RequestBody @Valid RoomRequest roomRequest, HttpServletRequest request){
        roomService.createARoom(roomRequest.getRoomType(), roomRequest.getImage(), roomRequest.getTextUrl3D());
        return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Room created successfully!", CREATED));
    }
    private URI getUri(){
        return URI.create("");
    }

}

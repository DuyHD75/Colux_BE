package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.RoomRequest;
import com.dcode.product_service.service.impl.RoomServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/products/rooms")
@AllArgsConstructor
public class RoomController {

    private final RoomServiceImpl roomService;

    @PostMapping
    public ResponseEntity<Response> createARoom(@RequestBody @Valid RoomRequest roomRequest, HttpServletRequest request){
        roomService.createARoom(roomRequest.getRoomType(),roomRequest.getHex(), roomRequest.getTitle()
                , roomRequest.getDescription(), roomRequest.getImage(), roomRequest.getTextUrl3D());
        return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Room created successfully!", CREATED));
    }
    @GetMapping("{roomId}")
    public ResponseEntity<Response> getARoom(@PathVariable("roomId")String roomId, HttpServletRequest request){
        var room = roomService.getARoom(roomId);
        return ResponseEntity.ok().body(getResponse(request, Map.of("room", room), "Room retrieve successfully!", OK));
    }
    @GetMapping("{roomId}/colors")
    public ResponseEntity<Response> getColorByRoom(@PathVariable("roomId")String roomId,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   HttpServletRequest request){
        Pageable pageable = PageRequest.of(page,size);
        var colors = roomService.getColorByRoom(roomId, pageable);
        return ResponseEntity.ok().body(getResponse(request, Map.of("colors", colors), "Retrieve color by roomId successfully!", OK));
    }
    @GetMapping
    public ResponseEntity<Response> getAllRoom(HttpServletRequest request){
        var rooms = roomService.getAllRoom();
        return ResponseEntity.ok().body(getResponse(request, Map.of("rooms", rooms), "Room retrieve successfully!", OK));
    }
    private URI getUri(){
        return URI.create("");
    }

}

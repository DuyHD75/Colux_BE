package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.RoomRequest;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.service.impl.RoomServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.dcode.product_service.utils.RequestUtils.getErrorResponse;
import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("/api/v1/rooms")
@AllArgsConstructor
public class RoomController {

    private final RoomServiceImpl roomService;

    @PreAuthorize("hasRole('EMPLOYEE') and hasAuthority('product:create')")
    @PostMapping
    public ResponseEntity<Response> createRooms(@RequestBody @Valid List<RoomRequest> roomRequest, HttpServletRequest request, HttpServletResponse response){
        try {
            roomService.createRooms(roomRequest);
            return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Room created successfully!", CREATED));

        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/public/roomId/{roomId}")
    public ResponseEntity<Response> getARoom(@PathVariable("roomId")String roomId, HttpServletRequest request,HttpServletResponse response){
        try {
            var room = roomService.getARoom(roomId);
            return ResponseEntity.ok().body(getResponse(request, Map.of("room", room), "Room retrieve successfully!", OK));
        }catch (ApiException ex) {
        return ResponseEntity.status(BAD_REQUEST)
                .body(getErrorResponse(request, response, ex, BAD_REQUEST));
    } catch (Exception exception) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
    }

    }
    @GetMapping("/public/roomId/{roomId}/colors")
    public ResponseEntity<Response> getColorByRoom(@PathVariable("roomId")String roomId,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response){
        try {
            Pageable pageable = PageRequest.of(page,size);
            var colors = roomService.getColorByRoom(roomId, pageable);
            return ResponseEntity.ok().body(getResponse(request, Map.of("colors", colors), "Retrieve color by roomId successfully!", OK));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }

    }
    @GetMapping("/public")
    public ResponseEntity<Response> getAllRoom(HttpServletRequest request, HttpServletResponse response){
        try{
            var rooms = roomService.getAllRoom();
            return ResponseEntity.ok().body(getResponse(request, Map.of("rooms", rooms), "Room retrieve successfully!", OK));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }

    }
    private URI getUri(){
        return URI.create("");
    }

}

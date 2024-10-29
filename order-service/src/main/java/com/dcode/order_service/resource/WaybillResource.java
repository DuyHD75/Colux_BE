package com.dcode.order_service.resource;

import com.dcode.order_service.domain.Response;
import com.dcode.order_service.dto.order.request.GhnCallbackOrderRequest;
import com.dcode.order_service.dto.order.request.WaybillRequest;
import com.dcode.order_service.service.WaybillService;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

import static com.dcode.order_service.utils.RequestUtils.getResponse;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/waybills")
@AllArgsConstructor
@CrossOrigin("http://localhost:3000")
public class WaybillResource {

    private WaybillService waybillService;

    @PutMapping("/callback-ghn")
    public ResponseEntity<ObjectNode> callbackStatusWaybillFromGHN(@RequestBody GhnCallbackOrderRequest ghnCallbackOrderRequest) {
        waybillService.callbackStatusWaybillFromGHN(ghnCallbackOrderRequest);
        return ResponseEntity.status(OK).body(new ObjectNode(JsonNodeFactory.instance));
    }

    @PostMapping("/create")
    public ResponseEntity<Response> createWaybill(@RequestBody WaybillRequest waybillRequest,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response) {
        var waybill = waybillService.createAWaybill(waybillRequest);
        return ResponseEntity.created(getUri()).body(
                getResponse(request, "Waybill created successfully!", CREATED, Map.of("waybill",waybill)));

    }
    private URI getUri() {
        return URI.create("/api/v1/waybills");
    }
}

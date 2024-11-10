package com.dcode.order_service.resource;

import com.dcode.order_service.domain.Response;
import com.dcode.order_service.service.impl.AdminServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.dcode.order_service.utils.RequestUtils.getResponse;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/admins")
@AllArgsConstructor
public class AdminResource {

    private final AdminServiceImpl adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<Response> getDashboard(HttpServletRequest request) {
        var DashboardResponse = adminService.getDashboard();
        return ResponseEntity.ok().body(getResponse(request, "Dashboard retrieved successfully!", OK, Map.of("dashboard", DashboardResponse)));
    }
    @GetMapping("/test")
    public ResponseEntity<Response> test(HttpServletRequest request) {
        return ResponseEntity.ok().body(getResponse(request, "Test retrieved successfully!", OK, Map.of("test", "Test")));
    }

}

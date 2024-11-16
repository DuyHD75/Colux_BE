package com.dcode.order_service.resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ws")
public class Socket {
    @RequestMapping("/test")
    public String getRoom() {
        return "Hello World!";
    }
}

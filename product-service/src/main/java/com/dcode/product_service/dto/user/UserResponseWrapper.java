package com.dcode.product_service.dto.user;

import lombok.Data;

import java.util.List;

@Data
public class UserResponseWrapper {
    private UserData data;

    @Data
    public static class UserData {
        private List<UserResponse> user;
    }
}
package com.dcode.product_service.service;



import com.dcode.product_service.domain.TokenData;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;
import java.util.function.Function;

public interface IJwtService {
    Optional<String> extractToken(HttpServletRequest request, String tokenType);

    <T> T getTokenData(String token, Function<TokenData, T> tokenFunction);
}

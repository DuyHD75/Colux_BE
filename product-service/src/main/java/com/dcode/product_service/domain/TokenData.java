package com.dcode.product_service.domain;


import io.jsonwebtoken.Claims;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Getter
@Setter
@Builder
public class TokenData {
    private String userId;
    private Claims claims;
    private boolean validToken;
    private List<GrantedAuthority> authorities;
}

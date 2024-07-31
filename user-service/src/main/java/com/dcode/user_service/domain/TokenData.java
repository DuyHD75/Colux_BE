package com.dcode.user_service.domain;


import com.dcode.user_service.dto.User;
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
    private User user;
    private Claims claims;
    private boolean validToken;
    private List<GrantedAuthority> authorities;
}

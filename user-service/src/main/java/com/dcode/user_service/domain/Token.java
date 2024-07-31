package com.dcode.user_service.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class Token {
    private String accessToken;
    private String refreshToken;
}

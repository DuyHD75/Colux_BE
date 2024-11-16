package com.dcode.order_service.service.impl;


import com.dcode.order_service.domain.TokenData;
import com.dcode.order_service.security.JwtConfiguration;
import com.dcode.order_service.service.IJwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.dcode.order_service.constant.Constants.AppConstants.*;
import static io.jsonwebtoken.Header.JWT_TYPE;
import static io.jsonwebtoken.Header.TYPE;
import static java.util.Arrays.stream;
import static java.util.Optional.empty;
import static org.springframework.security.core.authority.AuthorityUtils.commaSeparatedStringToAuthorityList;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl extends JwtConfiguration implements IJwtService {



    private final Supplier<SecretKey> key = () -> Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(getSecret()));

    private final Function<String, Claims> claimsFunction = token ->
            Jwts.parser()
                    .verifyWith(key.get())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

    private <T> T getClaimsValue(String token, Function<Claims, T> claims) {
        return claimsFunction.andThen(claims).apply(token); // getSubject
    }

    private final Function<String, String> subject = token -> getClaimsValue(token, Claims::getSubject);

    private final BiFunction<HttpServletRequest, String, Optional<String>> extractToken = (request, cookieName) ->
            Optional.of(
                    stream(request.getCookies() == null ? new Cookie[]{new Cookie(EMPTY_VALUE, EMPTY_VALUE)} : request.getCookies())
                            .filter(cookie -> Objects.equals(cookie.getName(), cookieName))
                            .map(Cookie::getValue)
                            .findAny()
            ).orElse(empty());

    private final BiFunction<HttpServletRequest, String, Optional<Cookie>> extractCookie = (request, cookieName) ->
            Optional.of(
                    stream(request.getCookies() == null ? new Cookie[]{new Cookie(EMPTY_VALUE, EMPTY_VALUE)} : request.getCookies())
                            .filter(cookie -> Objects.equals(cookie.getName(), cookieName))
                            .findAny()
            ).orElse(empty());


    private final Supplier<JwtBuilder> builder = () ->
            Jwts.builder()
                    .header().add(Map.of(TYPE, JWT_TYPE))
                    .and()
                    .audience().add(DUY_CODE_LLC)
                    .and()
                    .id(UUID.randomUUID().toString())
                    .signWith(key.get(), Jwts.SIG.HS512)
                    .issuedAt(Date.from(Instant.now()))
                    .notBefore(new Date());

    public Function<String, List<GrantedAuthority>> authorities = token ->
            commaSeparatedStringToAuthorityList(
                    new StringJoiner(AUTHORITY_DELIMITER)
                            .add(claimsFunction.apply(token).get(AUTHORITIES, String.class))
                            .add(ROLE_PREFIX + claimsFunction.apply(token).get(ROLE, String.class)).toString()
            );

    @Override
    public Optional<String> extractToken(HttpServletRequest request, String cookieName) {
        return extractToken.apply(request, cookieName);
    }

    @Override
    public <T> T getTokenData(String token, Function<TokenData, T> tokenFunction) {
        return tokenFunction.apply(
                TokenData.builder()
                        .validToken(claimsFunction.apply(token).getSubject() != null)
                        .authorities(authorities.apply(token))
                        .claims(claimsFunction.apply(token))
                        .userId(claimsFunction.apply(token).getSubject())
                        .build()
        );
    }
}








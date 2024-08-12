package com.dcode.identity_service.service.impl;

import com.dcode.identity_service.domain.Token;
import com.dcode.identity_service.domain.TokenData;
import com.dcode.identity_service.dto.User;
import com.dcode.identity_service.enumeration.TokenType;
import com.dcode.identity_service.function.TriConsumer;
import com.dcode.identity_service.security.JwtConfiguration;
import com.dcode.identity_service.service.IJwtService;
import com.dcode.identity_service.service.IUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.dcode.identity_service.constant.Constants.AuthorityConstant.*;
import static com.dcode.identity_service.enumeration.TokenType.ACCESS_TOKEN;
import static io.jsonwebtoken.Header.JWT_TYPE;
import static io.jsonwebtoken.Header.TYPE;
import static org.apache.tomcat.util.http.SameSiteCookies.NONE;
import static java.util.Arrays.stream;
import static java.util.Optional.empty;
import static org.springframework.security.core.authority.AuthorityUtils.commaSeparatedStringToAuthorityList;


@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl extends JwtConfiguration implements IJwtService {

    private final IUserService userService;

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

    private final BiFunction<User, TokenType, String> buildToken = (user, type) ->
            Objects.equals(type, ACCESS_TOKEN) ? builder.get()
                    .subject(user.getUserId())
                    .claim(AUTHORITIES, user.getAuthorities())
                    .claim(ROLE, user.getRole())
                    .expiration(Date.from(Instant.now().plusSeconds(getExpiration())))
                    .compact() : builder.get()
                    .subject(user.getUserId())
                    .expiration(Date.from(Instant.now().plusSeconds(getExpiration())))
                    .compact();

    private final TriConsumer<HttpServletResponse, User, TokenType> addCookie = (response, user, tokenType) -> {
        switch (tokenType) {
            case ACCESS_TOKEN -> {
                var accessToken = createToken(user, Token::getAccessToken);
                var cookie = new Cookie(tokenType.getValue(), accessToken);
                cookie.setHttpOnly(true);
                /* cookie.setSecure(true);*/
                cookie.setMaxAge(2 * 60);
                cookie.setPath("/");
                cookie.setAttribute("SameSite", NONE.name());
                response.addCookie(cookie);
            }
            case REFRESH_TOKEN -> {
                var refreshToken = createToken(user, Token::getRefreshToken);
                var cookie = new Cookie(tokenType.getValue(), refreshToken);
                cookie.setHttpOnly(true);
                /*cookie.setSecure(true);*/
                cookie.setMaxAge(2 * 60 * 60);
                cookie.setPath("/");
                cookie.setAttribute("SameSite", NONE.name());
                response.addCookie(cookie);
            }
        }
    };

    public Function<String, List<GrantedAuthority>> authorities = token ->
            commaSeparatedStringToAuthorityList(
                    new StringJoiner(AUTHORITY_DELIMITER)
                            .add(claimsFunction.apply(token).get(AUTHORITIES, String.class))
                            .add(ROLE_PREFIX + claimsFunction.apply(token).get(ROLE, String.class)).toString()
            );

    @Override
    public String createToken(User user, Function<Token, String> tokenFunction) {
        var token = Token.builder()
                .accessToken(buildToken.apply(user, ACCESS_TOKEN))
                .refreshToken(buildToken.apply(user, TokenType.REFRESH_TOKEN))
                .build();
        return tokenFunction.apply(token);
    }

    @Override
    public Optional<String> extractToken(HttpServletRequest request, String cookieName) {
        return extractToken.apply(request, cookieName);
    }

    @Override
    public void addCookie(HttpServletResponse response, User user, TokenType tokenType) {
        addCookie.accept(response, user, tokenType);
    }

    @Override
    public <T> T getTokenData(String token, Function<TokenData, T> tokenFunction) {
        return tokenFunction.apply(
                TokenData.builder()
                        .validToken(Objects.equals(userService.getUserByUserId(subject.apply(token)).getUserId(),
                                claimsFunction.apply(token).getSubject()))
                        .authorities(authorities.apply(token))
                        .claims(claimsFunction.apply(token))
                        .user(userService.getUserByUserId(subject.apply(token)))
                        .build()
        );
    }

    @Override
    public void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        extractCookie.apply(request, cookieName).ifPresent(cookie -> {
            cookie.setMaxAge(0);
            cookie.setValue(EMPTY_VALUE);
            cookie.setPath("/");
            response.addCookie(cookie);
        });
    }
}








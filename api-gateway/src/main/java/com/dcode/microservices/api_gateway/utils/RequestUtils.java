package com.dcode.microservices.api_gateway.utils;

import com.dcode.microservices.api_gateway.domain.Response;
import com.dcode.microservices.api_gateway.exception.ApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class RequestUtils {

    private static final BiConsumer<HttpServletResponse, Response> writeResponse = (httpServletResponse, response) -> {
        try {
            var outputStream = httpServletResponse.getOutputStream();
            new ObjectMapper().writeValue(outputStream, response);
            outputStream.flush();
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    };

    private static final BiFunction<Exception, HttpStatus, String> errorReason = (exception, status) -> {
        if (status.isSameCodeAs(FORBIDDEN)) return "You are not authorized to access this resource";

        if (status.isSameCodeAs(UNAUTHORIZED)) return "You are not authenticated to access this resource";

        if (exception instanceof AccessDeniedException) return "You are not authorized to access this resource";

        if (status.is5xxServerError()) return "An external error occurred while processing your request";
        else return "An error occurred while processing your request. Please try again later";
    };


    public static Response getResponse(HttpServletRequest request, Map<?, ?> data, String message, HttpStatus status) {
        return new Response(now().toString(), status.value(),
                request.getRequestURI(), HttpStatus.valueOf(status.value()),
                message, EMPTY, data);
    }

    public static Mono<Void> getErrorResponse(ServerHttpRequest request, ServerHttpResponse response, Exception exception, HttpStatus status) throws JsonProcessingException {
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.setStatusCode(status);

        Response errorResponse = new Response(
                now().toString(),
                status.value(),
                request.getURI().getPath().toString(),
                status,
                exception.getMessage(),
                errorReason.apply(exception, status),
                emptyMap()
        );

        DataBuffer dataBuffer = response.bufferFactory().wrap(new ObjectMapper().writeValueAsBytes(errorResponse));
        return response.writeWith(Mono.just(dataBuffer));
    }
}


















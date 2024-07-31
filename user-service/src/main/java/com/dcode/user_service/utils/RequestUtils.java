package com.dcode.user_service.utils;


import com.dcode.user_service.domain.Response;
import com.dcode.user_service.exception.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

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

        if (exception instanceof DisabledException || exception instanceof AccessDeniedException || exception instanceof LockedException ||
                exception instanceof BadCredentialsException || exception instanceof ApiException || exception instanceof CredentialsExpiredException) {
            return exception.getMessage();
        }
        if (status.is5xxServerError()) return "An external error occurred while processing your request";
        else return "An error occurred while processing your request. Please try again later";
    };


    public static Response getResponse(HttpServletRequest request, Map<?, ?> data, String message, HttpStatus status) {
        return new Response(now().toString(), status.value(),
                request.getRequestURI(), HttpStatus.valueOf(status.value()),
                message, EMPTY, data);
    }

    public static void handleErrorResponse(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        if (exception instanceof AccessDeniedException) {
            var apiResponse = getErrorResponse(request, response, exception, FORBIDDEN);
            writeResponse.accept(response, apiResponse);
        }
        if(exception instanceof AuthenticationException){
            var apiResponse = getErrorResponse(request, response, exception, UNAUTHORIZED);
            writeResponse.accept(response, apiResponse);
        }
    }

    private static Response getErrorResponse(HttpServletRequest request, HttpServletResponse response, Exception exception, HttpStatus status) {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(status.value());
        return new Response(now().toString(), status.value(),
                request.getRequestURI(), HttpStatus.valueOf(status.value()),
                null, errorReason.apply(exception, status), emptyMap());
    }


}


















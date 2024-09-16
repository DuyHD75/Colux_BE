package com.dcode.order_service.config;


import com.dcode.order_service.dto.payment.AccessTokenResponse;
import com.dcode.order_service.dto.payment.PaypalRequest;
import com.dcode.order_service.dto.payment.PaypalResponse;
import com.dcode.order_service.enumuration.PaypalEndPoints;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

import static com.dcode.order_service.constant.Constants.AppConstants.PAYPAL_BASE_URL;

@Component
@AllArgsConstructor
@Slf4j
public class PaypalHttpClient {

    private final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
    private final PaypalConfig paypalConfig;
    private final ObjectMapper objectMapper;

    public String encodeBasicCredentials() {
        var credentials = paypalConfig.getClientId() + ":" + paypalConfig.getClientSecret();
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
    }



    private AccessTokenResponse getPaypalAccessToken () {
        try {
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(PaypalEndPoints.createURL(PAYPAL_BASE_URL, PaypalEndPoints.GET_ACCESS_TOKEN)))
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, encodeBasicCredentials())
                    .header(HttpHeaders.ACCEPT_LANGUAGE, "en_US")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                    .build();
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            var content = response.body();

            return objectMapper.readValue(content, AccessTokenResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Error while getting access token");
        }
    }

    public PaypalResponse createPaypalTransaction(PaypalRequest paypalRequest) throws Exception {
        var accessTokenResponse = getPaypalAccessToken();
        var payload = objectMapper.writeValueAsString(paypalRequest);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(PaypalEndPoints.createURL(PAYPAL_BASE_URL, PaypalEndPoints.ORDER_CHECKOUT)))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenResponse.getAccessToken())
                .header("Prefer", "return=representation")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        var content = response.body();

        return objectMapper.readValue(content, PaypalResponse.class);
    }

    public void capturePaypalTransaction(String paypalOrderId, String payerId) throws Exception {
        var accessTokenResponse = getPaypalAccessToken();

        var request = HttpRequest.newBuilder()
                .uri(URI.create(PaypalEndPoints.createCaptureURL(PAYPAL_BASE_URL, PaypalEndPoints.ORDER_CHECKOUT, paypalOrderId)))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenResponse.getAccessToken())
                .header("Prefer", "return=representation")
                .header("PayPal-Request-Id", payerId)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // TODO: Convert response to object if we need (Using debugger to check propeties response)
    }

}

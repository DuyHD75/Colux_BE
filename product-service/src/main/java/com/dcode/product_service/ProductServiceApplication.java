package com.dcode.product_service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@SpringBootApplication
@EnableFeignClients
@EnableAsync(proxyTargetClass = true)
@EnableCaching(proxyTargetClass = true)
public class ProductServiceApplication {

	public static void main(String[] args) throws IOException {





		SpringApplication.run(ProductServiceApplication.class, args);
	}

}

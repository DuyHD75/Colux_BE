package com.dcode.product_service.config;

import com.dcode.product_service.constant.Constants;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {


    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
//        FileInputStream serviceAccount =
//                new FileInputStream("product-service/src/main/resources/colux-alpha-storage.json");
        ClassPathResource serviceAccount = new ClassPathResource("colux-alpha-storage.json");

        try (InputStream serviceAccountStream = serviceAccount.getInputStream()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                    .setStorageBucket(Constants.BUCKET_NAME)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                return FirebaseApp.initializeApp(options);
            }
        }
        return null;
    }

}
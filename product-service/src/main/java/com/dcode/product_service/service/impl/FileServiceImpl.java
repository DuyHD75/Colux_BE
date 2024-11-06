package com.dcode.product_service.service.impl;

import com.dcode.product_service.service.IFileService;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.util.StringUtils;

import static com.dcode.product_service.constant.Constants.BUCKET_NAME;
import static com.dcode.product_service.constant.Constants.DOWNLOAD_URL;

@Service
public class FileServiceImpl implements IFileService {

    @Override
    public String save(MultipartFile multipartFile, String folderName) throws IOException {
        // Get the storage bucket
        Bucket bucket = StorageClient.getInstance().bucket();

        // Generate the blob name including folder path and a UUID to avoid conflicts
        String blobString = folderName + "/" + UUID.randomUUID().toString() + "_" + multipartFile.getOriginalFilename();

        // Create the blob in the bucket
        Blob blob = bucket.create(blobString, multipartFile.getInputStream(), multipartFile.getContentType());

        // Generate a download token
        String downloadToken = UUID.randomUUID().toString();

        // Update blob metadata to include a token
        BlobId blobId = BlobId.of(bucket.getName(), blobString); // Ensure we use blobString here
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(multipartFile.getContentType())
                .setMetadata(Map.of("firebaseStorageDownloadTokens", downloadToken)) // Add token metadata
                .build();
        bucket.getStorage().update(blobInfo); // Apply the update to set the download token

        // Construct the download URL
        String downloadLink = String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media&token=%s",
                bucket.getName(),
                URLEncoder.encode(blob.getName(), StandardCharsets.UTF_8), // Encode the blob name
                downloadToken
        );

        return downloadLink;
    }

    @Override
    public String save(BufferedImage bufferedImage, String originalFileName) throws IOException {
        byte[] bytes = getByteArrays(bufferedImage, getExtension(originalFileName));
        Bucket bucket = StorageClient.getInstance().bucket();
        String name = generateFileName(originalFileName);
        bucket.create(name, bytes);
        return name;
    }

    @Override
    public void delete(String name) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket();
        if (StringUtils.isEmpty(name)) {
            throw new IOException("invalid file name");
        }
        Blob blob = bucket.get(name);
        if (blob == null) {
            throw new IOException("file not found");
        }
        blob.delete();
    }

    @Override
    public String getExtension(String originalFileName) {
        return IFileService.super.getExtension(originalFileName);
    }

    @Override
    public String generateFileName(String originalFileName) {
        return IFileService.super.generateFileName(originalFileName);
    }

    @Override
    public byte[] getByteArrays(BufferedImage bufferedImage, String format) throws IOException {
        return IFileService.super.getByteArrays(bufferedImage, format);
    }


}

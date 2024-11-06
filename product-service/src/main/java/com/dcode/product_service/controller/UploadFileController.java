package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.service.IFileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/upload")
@Slf4j
@RequiredArgsConstructor
public class UploadFileController {

    private final IFileService fileService;

    @GetMapping("/pics")
    public ResponseEntity<Response> getUploadForm(HttpServletRequest request) {
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Upload file form", OK));
    }

    @PostMapping("/pics")
    public ResponseEntity<Response> upload(@RequestParam("file") MultipartFile[] multipartFiles, @RequestParam("folder") String folder, HttpServletRequest request) {
        try {
            List<String> fileUrls = new ArrayList<>();
            for (MultipartFile file : multipartFiles) {
                String fileName = fileService.save(file, folder);
                fileUrls.add(fileName);
            }
            return ResponseEntity.ok().body(getResponse(request, Map.of("fileUrls", fileUrls), "File uploaded successfully", OK));
        } catch (IOException e) {
            log.error("Failed to upload file", e);
            return ResponseEntity.status(500).body(getResponse(request, emptyMap(), "Failed to upload file", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
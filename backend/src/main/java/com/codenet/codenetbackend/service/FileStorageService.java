package com.codenet.codenetbackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {
    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    public String storeFile(MultipartFile file) throws IOException {
        // ** FOR PRODUCTION: INTEGRATE WITH AWS S3, GCP CLOUD STORAGE, OR AZURE BLOB STORAGE **
        // Example: using AWS S3 SDK (you'd add aws-java-sdk-s3 dependency)
        // S3Client s3Client = S3Client.builder().build();
        // String key = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        // s3Client.putObject(PutObjectRequest.builder().bucket("your-s3-bucket").key(key).build(),
        //                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        // return "https://your-s3-bucket.s3.amazonaws.com/" + key;

        // ** For local development (REMOVE FOR PRODUCTION DEPLOYMENT!) **
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath); // Create directory if it doesn't exist

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        file.transferTo(filePath);
        
        log.info("File stored: {}", fileName);
        return "/uploads/" + fileName; // Return a URL path
    }

    public void deleteFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            Files.deleteIfExists(filePath);
            log.info("File deleted: {}", fileName);
        } catch (IOException e) {
            log.error("Error deleting file: {}", fileName, e);
        }
    }

    public boolean fileExists(String fileName) {
        Path filePath = Paths.get(uploadDir).resolve(fileName);
        return Files.exists(filePath);
    }
} 
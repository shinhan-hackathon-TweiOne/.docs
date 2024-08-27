package com.shinhantime.tweione.Controller;

import com.shinhantime.tweione.Service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/s3")
public class S3Controller {

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("bucketName") String bucketName,
                                             @RequestParam("keyName") String keyName) {
        try {
            String key = s3Service.uploadFile(bucketName, keyName, file);
            return ResponseEntity.ok("File uploaded successfully. Key: " + key);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload file: " + e.getMessage());
        }
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam("bucketName") String bucketName,
                                               @RequestParam("keyName") String keyName) {
        byte[] file = s3Service.downloadFile(bucketName, keyName);
        return ResponseEntity.ok(file);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam("bucketName") String bucketName,
                                             @RequestParam("keyName") String keyName) {
        s3Service.deleteFile(bucketName, keyName);
        return ResponseEntity.ok("File deleted successfully.");
    }
}
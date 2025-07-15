package com.knit.api.controller.file;

import com.knit.api.config.aws.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/files")
@RestController
public class FileController {

    private final S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
            }
            
            String fileUrl = s3Service.uploadFile(file);
            return ResponseEntity.ok(Map.of(
                "url", fileUrl,
                "filename", file.getOriginalFilename(),
                "size", String.valueOf(file.getSize())
            ));
        } catch (IOException e) {
            log.error("File upload failed", e);
            return ResponseEntity.badRequest().body(Map.of("error", "File upload failed: " + e.getMessage()));
        }
    }

    @PostMapping("/upload/multiple")
    public ResponseEntity<?> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        try {
            if (files == null || files.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No files provided"));
            }
            
            List<String> fileUrls = s3Service.uploadFiles(files);
            return ResponseEntity.ok(Map.of(
                "urls", fileUrls,
                "count", files.size()
            ));
        } catch (Exception e) {
            log.error("Files upload failed", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Files upload failed: " + e.getMessage()));
        }
    }
}
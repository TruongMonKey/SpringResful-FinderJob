package com.example.JobFinder.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.JobFinder.domain.response.ResUploadFileDTO;
import com.example.JobFinder.service.FileService;
import com.example.JobFinder.util.annotation.ApiMessage;
import com.example.JobFinder.util.errors.StorageException;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Value("${monkey.upload-file.base-uri}")
    private String baseURI;

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    @ApiMessage("Upload single file successfully")
    public ResponseEntity<ResUploadFileDTO> handleFileUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("folder") String folder)
            throws URISyntaxException, IOException, StorageException {

        if (file == null || file.isEmpty()) {
            throw new StorageException("File is empty. Please upload a valid file.");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new StorageException("File name is invalid.");
        }

        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValid = allowedExtensions.stream()
                .anyMatch(ext -> fileName.toLowerCase().endsWith("." + ext));

        if (!isValid) {
            throw new StorageException(
                    "File format not supported. Please upload one of: " + allowedExtensions);
        }

        this.fileService.createDirectory(baseURI + folder);

        String uploadedPath = this.fileService.store(file, folder);
        ResUploadFileDTO res = new ResUploadFileDTO(uploadedPath, Instant.now());

        return ResponseEntity.ok(res);
    }

    @GetMapping("/download")
    @ApiMessage("Download file successfully")
    public ResponseEntity<Resource> downloadFile(
            @RequestParam("file") String fileName,
            @RequestParam("folder") String folder)
            throws FileNotFoundException, URISyntaxException, StorageException {

        if (fileName == null || folder == null || fileName.isBlank() || folder.isBlank()) {
            throw new StorageException("Missing required parameters: 'file' or 'folder'.");
        }

        long fileLength = this.fileService.getFileLenght(fileName, folder);
        if (fileLength == 0) {
            throw new StorageException("File with name '" + fileName + "' not found.");
        }

        InputStreamResource resource = this.fileService.getResource(fileName, folder);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentLength(fileLength)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}

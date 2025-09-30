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
    @ApiMessage("Upload single file")
    public ResponseEntity<ResUploadFileDTO> handleFileUpload(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("folder") String folder) throws URISyntaxException, IOException, StorageException {
        if (file == null || file.isEmpty()) {
            throw new StorageException("File is empty. Please upload");
        }
        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValid = allowedExtensions.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));

        if (!isValid) {
            throw new StorageException(
                    "File is not in correct format, please upload again." + allowedExtensions.toString());
        }

        this.fileService.createDirectory(baseURI + folder);

        String uploadFile = this.fileService.store(file, folder);
        ResUploadFileDTO res = new ResUploadFileDTO(uploadFile, Instant.now());

        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/download")
    @ApiMessage("Download a file")
    public ResponseEntity<Resource> download(
            @RequestParam(name = "file", required = false) String fileName,
            @RequestParam(name = "folder", required = false) String folder)
            throws FileNotFoundException, URISyntaxException, StorageException {
        if (fileName == null || folder == null) {
            throw new StorageException("Missing required params(file or folder null)");
        }

        long fileLenght = this.fileService.getFileLenght(fileName, folder);
        if (fileLenght == 0) {
            throw new StorageException("File with name: " + fileName + " not found");
        }

        InputStreamResource resource = this.fileService.getResource(fileName, folder);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentLength(fileLenght)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

}

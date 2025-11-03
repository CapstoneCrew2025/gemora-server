package com.gemora_server.service;

import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;

public interface FileStorageService {
    String storeGemImage(MultipartFile file);
    String storeCertificateFile(MultipartFile file);
    Path getFilePath(String fileName, boolean certificate);
    void deleteFile(String fileName, boolean certificate);

}

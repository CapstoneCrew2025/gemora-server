package com.gemora_server.service.impl;

import com.gemora_server.exception.BusinessException;
import com.gemora_server.service.FileStorageService;
import com.gemora_server.util.FileStorageProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path gemsLocation;
    private final Path certsLocation;

    public FileStorageServiceImpl(FileStorageProperties properties) {
        this.gemsLocation = Paths.get(properties.getGemsDir()).toAbsolutePath().normalize();
        this.certsLocation = Paths.get(properties.getCertificatesDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.gemsLocation);
            Files.createDirectories(this.certsLocation);
        } catch (Exception ex) {
            throw new BusinessException("Could not create upload directories");
        }
    }

    @Override
    public String storeGemImage(MultipartFile file) {
        return storeFile(file, gemsLocation);
    }

    @Override
    public String storeCertificateFile(MultipartFile file) {
        return storeFile(file, certsLocation);
    }

    private String storeFile(MultipartFile file, Path targetLocation) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = "";
        int i = originalFileName.lastIndexOf('.');
        if (i >= 0) ext = originalFileName.substring(i);
        String fileName = UUID.randomUUID().toString() + ext;
        try {
            if (fileName.contains("..")) {
                throw new BusinessException("Invalid path sequence " + fileName);
            }
            Path destination = targetLocation.resolve(fileName);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex) {
            throw new BusinessException("Could not store file " + originalFileName);
        }
    }

    @Override
    public Path getFilePath(String fileName, boolean certificate) {
        return (certificate ? certsLocation : gemsLocation).resolve(fileName).normalize();
    }

    @Override
    public void deleteFile(String fileName, boolean certificate) {
        try {
            Path filePath = (certificate ? certsLocation : gemsLocation).resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new BusinessException("Could not delete file: " + fileName);
        }
    }


}

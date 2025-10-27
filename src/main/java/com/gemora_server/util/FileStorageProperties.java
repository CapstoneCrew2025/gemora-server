package com.gemora_server.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {

    private String uploadDir;
    private String gemsDir;
    private String certificatesDir;

    public String getUploadDir() { return uploadDir; }
    public void setUploadDir(String uploadDir) { this.uploadDir = uploadDir; }

    public String getGemsDir() { return gemsDir; }
    public void setGemsDir(String gemsDir) { this.gemsDir = gemsDir; }

    public String getCertificatesDir() { return certificatesDir; }
    public void setCertificatesDir(String certificatesDir) { this.certificatesDir = certificatesDir; }

}

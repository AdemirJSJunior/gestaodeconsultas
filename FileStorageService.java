package com.gestaodeh.gestaodeh.services;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
@Service
public class FileStorageService {
    private final Path fileStorageLocation;
    public FileStorageService(@Value("${gestaodeh.app.uploadDir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try { Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) { throw new RuntimeException("Não foi possível criar o diretório para guardar os ficheiros.", ex); }
    }
    public String storeFile(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;
        try {
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation);
            return uniqueFileName;
        } catch (IOException ex) { throw new RuntimeException("Não foi possível guardar o ficheiro " + originalFileName, ex); }
    }
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) { return resource; }
            else { throw new RuntimeException("Ficheiro não encontrado " + fileName); }
        } catch (MalformedURLException ex) { throw new RuntimeException("Ficheiro não encontrado " + fileName, ex); }
    }
}

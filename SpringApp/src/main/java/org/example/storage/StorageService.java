package org.example.storage;

import org.example.service.FileSaveFormat;
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
public class StorageService {

    @Value("${storage.location}")
    private String location;  // Це має бути шлях до папки "uploading"

    // Ініціалізація папки для збереження файлів
    public void init() throws IOException {
        Path path = Paths.get(location);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    // Збереження файлу
    public String save(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path path = Paths.get(location + File.separator + fileName);
        Files.copy(file.getInputStream(), path);
        return fileName;
    }

    // Збереження зображень
    public String saveImage(MultipartFile file, FileSaveFormat format) throws IOException {
        String fileName = UUID.randomUUID().toString() + "." + format.getExtension();
        Path path = Paths.get(location + File.separator + fileName);
        Files.copy(file.getInputStream(), path);
        return fileName;
    }

    // Метод для завантаження зображень з URL
    public String saveImage(String fileUrl, FileSaveFormat format) throws IOException {
        String fileName = UUID.randomUUID().toString() + "." + format.getExtension();
        Path path = Paths.get(location + File.separator + fileName);
        // Логіка завантаження зображення з URL
        return fileName;
    }
}

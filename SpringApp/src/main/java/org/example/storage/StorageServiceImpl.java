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
public class StorageServiceImpl extends StorageService {

    @Value("${storage.location}")
    private String location; // Папка для збереження файлів

    @Override
    public void init() throws IOException {
        // Ініціалізація каталогу для збереження файлів (якщо він не існує)
        Path path = Paths.get(location);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    @Override
    public String save(MultipartFile file) throws IOException {
        // Генерація унікального імені для файлу
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path path = Paths.get(location + File.separator + fileName);

        // Збереження файлу на диск
        Files.copy(file.getInputStream(), path);
        return fileName;
    }

    @Override
    public String saveImage(MultipartFile file, FileSaveFormat format) throws IOException {
        // Логіка для збереження зображень з урахуванням формату
        String fileName = UUID.randomUUID().toString() + "." + format.getExtension();
        Path path = Paths.get(location + File.separator + fileName);

        // Збереження файлу на диск
        Files.copy(file.getInputStream(), path);
        return fileName;
    }

    @Override
    public String saveImage(String fileUrl, FileSaveFormat format) throws IOException {
        // Логіка для збереження зображень із URL (якщо потрібно)
        String fileName = UUID.randomUUID().toString() + "." + format.getExtension();
        Path path = Paths.get(location + File.separator + fileName);

        // Завантаження файлу з URL та збереження
        // Наприклад, можна використовувати HTTP-клієнт для завантаження зображення за URL
        // В даному випадку тут можна реалізувати відповідну логіку.

        return fileName;
    }
}

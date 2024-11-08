package org.example.storage.impl;

import org.example.service.FileSaveFormat;
import org.example.storage.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileSystemStorageService extends StorageService {

    private final Path rootLocation = Paths.get("uploadings"); // Де зберігаються файли

    @Override
    public void init() throws IOException {
        if (!Files.exists(rootLocation)) {
            Files.createDirectories(rootLocation);  // Створюємо директорію, якщо вона не існує
        }
    }

    @Override
    public String save(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        Path destinationPath = rootLocation.resolve(fileName);
        file.transferTo(destinationPath.toFile());
        return destinationPath.toString();  // Повертаємо шлях до файлу
    }

    @Override
    public String saveImage(MultipartFile file, FileSaveFormat format) throws IOException {
        String fileName = file.getOriginalFilename();
        if (format != null) {
            fileName = fileName.replaceFirst("[.][^.]+$", "") + "." + format.getExtension();
        }
        Path destinationPath = rootLocation.resolve(fileName);
        file.transferTo(destinationPath.toFile());
        return destinationPath.toString();  // Повертаємо шлях до збереженого файлу
    }

    @Override
    public String saveImage(String fileUrl, FileSaveFormat format) throws IOException {
        // Якщо ви хочете працювати з URL або іншими джерелами, реалізуйте це тут
        return fileUrl;
    }
}

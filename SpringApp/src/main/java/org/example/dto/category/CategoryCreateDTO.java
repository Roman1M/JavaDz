package org.example.dto.category;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CategoryCreateDTO {
    private String name;           // Назва категорії
    private MultipartFile image;   // Файл зображення для категорії
    private String description;    // Опис категорії
}

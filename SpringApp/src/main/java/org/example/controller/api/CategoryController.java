package org.example.controller.api;

import lombok.AllArgsConstructor;
import org.example.dto.category.CategoryCreateDTO;
import org.example.dto.category.CategoryItemDTO;
import org.example.mapper.CategoryMapper;
import org.example.model.CategoryEntity;
import org.example.repo.CategoryRepository;
import org.example.service.FileSaveFormat;
import org.example.storage.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/category")
public class CategoryController {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final StorageService storageService;

    // Створення категорії
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Integer> create(@ModelAttribute CategoryCreateDTO dto) {
        try {
            // Створення категорії з DTO
            CategoryEntity entity = categoryMapper.categoryEntityByCategoryCreateDTO(dto);
            entity.setCreationTime(LocalDateTime.now());

            // Збереження зображення для категорії
            String fileName = storageService.saveImage(dto.getImage(), FileSaveFormat.webp);
            entity.setImage(fileName);

            // Збереження категорії в базу даних
            categoryRepository.save(entity);
            return new ResponseEntity<>(entity.getId(), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Отримання всіх категорій
    @GetMapping("")
    public ResponseEntity<List<CategoryItemDTO>> getAllCategories() {
        var categories = categoryMapper.toDto(categoryRepository.findAll());
        return ResponseEntity.ok(categories);
    }
}

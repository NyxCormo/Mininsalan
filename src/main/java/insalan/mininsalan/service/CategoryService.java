package insalan.mininsalan.service;

import insalan.mininsalan.dto.CategoryDto;
import insalan.mininsalan.entity.Category;
import insalan.mininsalan.exception.ResourceNotFoundException;
import insalan.mininsalan.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    /**
     * Get all categories
     * Used for: Category selection in challenge management
     */
    @Cacheable("categories")
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        logger.info("Fetching all categories");
        try {
            List<Category> categories = categoryRepository.findAll();
            return categories.stream()
                    .map(this::toCategoryDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching categories: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get a single category by ID
     * Used for: Category details
     */
    @Cacheable(value = "category", key = "#categoryId")
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long categoryId) {
        logger.info("Fetching category with ID: {}", categoryId);
        try {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
            return toCategoryDto(category);
        } catch (Exception e) {
            logger.error("Error fetching category {}: {}", categoryId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Create a new category
     * Used for: Admin category creation
     */
    @CacheEvict(value = {"categories", "category"}, allEntries = true)
    public CategoryDto createCategory(CategoryDto dto) {
        logger.info("Creating new category: {}", dto.getName());
        try {
            validateCategoryDto(dto);

            Category category = Category.builder()
                    .name(dto.getName())
                    .build();

            Category savedCategory = categoryRepository.save(category);
            logger.info("Created category with ID: {}", savedCategory.getId());

            return toCategoryDto(savedCategory);
        } catch (Exception e) {
            logger.error("Error creating category: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Update an existing category
     * Used for: Admin category modification
     */
    @CacheEvict(value = {"categories", "category"}, allEntries = true)
    public CategoryDto updateCategory(Long categoryId, CategoryDto dto) {
        logger.info("Updating category with ID: {}", categoryId);
        try {
            Category existingCategory = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

            validateCategoryDto(dto);

            existingCategory.setName(dto.getName());

            Category savedCategory = categoryRepository.save(existingCategory);
            logger.info("Updated category with ID: {}", savedCategory.getId());

            return toCategoryDto(savedCategory);
        } catch (Exception e) {
            logger.error("Error updating category {}: {}", categoryId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Delete a category
     * Used for: Admin category removal
     */
    @CacheEvict(value = {"categories", "category"}, allEntries = true)
    public void deleteCategory(Long categoryId) {
        logger.info("Deleting category with ID: {}", categoryId);
        try {
            if (!categoryRepository.existsById(categoryId)) {
                throw new ResourceNotFoundException("Category not found with id: " + categoryId);
            }

            categoryRepository.deleteById(categoryId);
            logger.info("Deleted category with ID: {}", categoryId);
        } catch (Exception e) {
            logger.error("Error deleting category {}: {}", categoryId, e.getMessage(), e);
            throw e;
        }
    }

    // Helper method to convert Category entity to DTO
    private CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    // Validation logic for CategoryDto
    private void validateCategoryDto(CategoryDto dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
    }
}
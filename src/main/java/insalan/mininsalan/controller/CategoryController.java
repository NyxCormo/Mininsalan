package insalan.mininsalan.controller;

import insalan.mininsalan.dto.CategoryDto;
import insalan.mininsalan.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Get all categories
     * Used for: Category selection in challenge management
     */
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    /**
     * Get a single category by ID
     * Used for: Category details
     */
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.getCategoryById(categoryId));
    }

    /**
     * Create a new category
     * Used for: Admin category creation
     */
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto dto) {
        return ResponseEntity.ok(categoryService.createCategory(dto));
    }

    /**
     * Update an existing category
     * Used for: Admin category modification
     */
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody CategoryDto dto) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, dto));
    }

    /**
     * Delete a category
     * Used for: Admin category removal
     */
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
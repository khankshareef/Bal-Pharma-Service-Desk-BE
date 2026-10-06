package Service_Desk.BalPharma.category.controller;

import Service_Desk.BalPharma.category.dto.*;
import Service_Desk.BalPharma.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponseDto> create(@RequestBody CreateCategoryDto dto) {
        return ResponseEntity.ok(categoryService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @GetMapping("/stats")
    public ResponseEntity<CategoryStatsDto> getStats() {
        return ResponseEntity.ok(categoryService.getStats());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @GetMapping("/by-department/{departmentId}")
    public ResponseEntity<List<CategoryResponseDto>> getByDepartment(
            @PathVariable Long departmentId) {
        return ResponseEntity.ok(categoryService.getByDepartment(departmentId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> update(@PathVariable Long id,
                                                      @RequestBody UpdateCategoryDto dto) {
        return ResponseEntity.ok(categoryService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok("Category deleted");
    }
}
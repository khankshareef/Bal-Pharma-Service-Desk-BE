package Service_Desk.BalPharma.category.service;

import Service_Desk.BalPharma.category.dto.*;
import Service_Desk.BalPharma.category.entity.CategoryEntity;
import Service_Desk.BalPharma.category.entity.SubCategoryEntity;
import Service_Desk.BalPharma.category.repository.CategoryRepository;
import Service_Desk.BalPharma.category.repository.SubCategoryRepository;
import Service_Desk.BalPharma.department.entity.DepartmentEntity;
import Service_Desk.BalPharma.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import Service_Desk.BalPharma.department.repository.DepartmentRepository;
import Service_Desk.BalPharma.department.entity.DepartmentEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final DepartmentRepository departmentRepository;
    private final SubCategoryRepository subCategoryRepository;

    @Override
    public CategoryResponseDto create(CreateCategoryDto dto) {
        if (dto.getName() == null || dto.getName().isBlank())
            throw new AuthException("Category name is required");
        if (dto.getDepartmentId() == null)
            throw new AuthException("Department is required");

        if (categoryRepository.existsByNameIgnoreCase(dto.getName().trim()))
            throw new AuthException("Category already exists: " + dto.getName());

        DepartmentEntity dep = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new AuthException("Department not found"));

        CategoryEntity c = new CategoryEntity();
        c.setName(dto.getName().trim());
        c.setCategoryCode(generateCode(dto.getName()));
        c.setDepartment(dep);
        c.setScope(dto.getScope() != null ? dto.getScope().trim() : "All Units");
        c.setActive(dto.getActive() != null ? dto.getActive() : true);

        if (dto.getSubCategories() != null) {
            for (String s : dto.getSubCategories()) {
                if (s == null || s.isBlank()) continue;
                SubCategoryEntity sub = new SubCategoryEntity();
                sub.setName(s.trim());
                sub.setActive(true);
                sub.setCategory(c);
                c.getSubCategories().add(sub);
            }
        }

        CategoryEntity saved = categoryRepository.save(c);
        return CategoryResponseDto.from(
                categoryRepository.findByIdWithRelations(saved.getId()).orElse(saved)
        );
    }

    @Override
    public List<CategoryResponseDto> getByDepartment(Long departmentId) {
        return categoryRepository.findByDepartmentIdWithSubCategories(departmentId).stream()
                .map(CategoryResponseDto::from)
                .toList();
    }

    @Override
    public List<CategoryResponseDto> getAll() {
        return categoryRepository.findAllWithRelations().stream()
                .map(CategoryResponseDto::from)
                .toList();
    }

    @Override
    public CategoryResponseDto getById(Long id) {
        return CategoryResponseDto.from(categoryRepository.findById(id)
                .orElseThrow(() -> new AuthException("Category not found: " + id)));
    }

    @Override
    public CategoryResponseDto update(Long id, UpdateCategoryDto dto) {
        CategoryEntity c = categoryRepository.findById(id)
                .orElseThrow(() -> new AuthException("Category not found: " + id));

        if (dto.getName() != null && !dto.getName().isBlank())
            c.setName(dto.getName().trim());

        if (dto.getScope() != null)
            c.setScope(dto.getScope().trim());

        if (dto.getActive() != null)
            c.setActive(dto.getActive());

        if (dto.getSubCategories() != null) {
            c.getSubCategories().clear();
            for (String s : dto.getSubCategories()) {
                if (s == null || s.isBlank()) continue;
                SubCategoryEntity sub = new SubCategoryEntity();
                sub.setName(s.trim());
                sub.setActive(true);
                sub.setCategory(c);
                c.getSubCategories().add(sub);
            }
        }

        return CategoryResponseDto.from(categoryRepository.save(c));
    }

    @Override
    public void delete(Long id) {
        if (!categoryRepository.existsById(id))
            throw new AuthException("Category not found: " + id);
        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryStatsDto getStats() {
        long total    = categoryRepository.count();
        long active   = categoryRepository.countByActive(true);
        long inactive = categoryRepository.countByActive(false);

        long subCount = categoryRepository.findAll().stream()
                .mapToLong(c -> c.getSubCategories().size())
                .sum();

        return new CategoryStatsDto(total, active, inactive, subCount);
    }

    private String generateCode(String name) {
        String slug = name.trim().toUpperCase().replaceAll("[^A-Z0-9]+", "_");
        return "CAT_" + slug + "_" + System.currentTimeMillis();
    }
}
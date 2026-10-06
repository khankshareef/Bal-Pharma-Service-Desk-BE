package Service_Desk.BalPharma.category.service;

import Service_Desk.BalPharma.category.dto.*;

import java.util.List;

public interface CategoryService {
    CategoryResponseDto create(CreateCategoryDto dto);
    List<CategoryResponseDto> getAll();
    CategoryResponseDto getById(Long id);
    CategoryResponseDto update(Long id, UpdateCategoryDto dto);
    List<CategoryResponseDto> getByDepartment(Long departmentId);
    void delete(Long id);
    CategoryStatsDto getStats();
}
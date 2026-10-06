package Service_Desk.BalPharma.department.service;

import Service_Desk.BalPharma.department.dto.*;

import java.util.List;

public interface DepartmentService {
    DepartmentResponseDto create(CreateDepartmentDto dto);
    List<DepartmentResponseDto> getAll();
    DepartmentResponseDto getById(Long id);
    DepartmentResponseDto update(Long id, UpdateDepartmentDto dto);
    void delete(Long id);
    DepartmentStatsDto getStats();
}
package Service_Desk.BalPharma.department.service;

import Service_Desk.BalPharma.auth.entity.AuthEntity;
import Service_Desk.BalPharma.auth.repository.AuthRepository;
import Service_Desk.BalPharma.department.dto.*;
import Service_Desk.BalPharma.department.entity.DepartmentEntity;
import Service_Desk.BalPharma.department.repository.DepartmentRepository;
import Service_Desk.BalPharma.exception.AuthException;
import Service_Desk.BalPharma.unit.entity.UnitEntity;
import Service_Desk.BalPharma.unit.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UnitRepository unitRepository;
    private final AuthRepository authRepository;

    @Override
    public DepartmentResponseDto create(CreateDepartmentDto dto) {

        if (dto.getName() == null || dto.getName().isBlank())
            throw new AuthException("Department name is required");

        if (departmentRepository.existsByNameIgnoreCase(dto.getName().trim()))
            throw new AuthException("Department already exists: " + dto.getName());

        DepartmentEntity dep = new DepartmentEntity();
        dep.setName(dto.getName().trim());
        dep.setDepartmentCode(generateCode(dto.getName()));
        dep.setActive(dto.getActive() != null ? dto.getActive() : true);

        if (dto.getUnitId() != null) {
            UnitEntity unit = unitRepository.findById(dto.getUnitId())
                    .orElseThrow(() -> new AuthException("Unit not found: " + dto.getUnitId()));
            dep.setUnit(unit);
        }

        DepartmentEntity saved = departmentRepository.save(dep);

        return DepartmentResponseDto.from(
                departmentRepository.findByIdWithUnit(saved.getId()).orElse(saved)
        );
    }

    @Override
    public List<DepartmentResponseDto> getAll() {

        List<DepartmentEntity> departments = departmentRepository.findAllWithUnit();

        Map<String, Long> userCounts = authRepository.findAll().stream()
                .filter(a -> a.getDepartment() != null && !a.getDepartment().isBlank())
                .collect(Collectors.groupingBy(
                        AuthEntity::getDepartment,
                        Collectors.counting()
                ));

        return departments.stream()
                .map(d -> {
                    DepartmentResponseDto dto = DepartmentResponseDto.from(d);
                    dto.setUsers(userCounts.getOrDefault(d.getName(), 0L));
                    return dto;
                })
                .toList();
    }

    @Override
    public DepartmentResponseDto getById(Long id) {

        DepartmentEntity dep = departmentRepository.findByIdWithUnit(id)
                .orElseThrow(() -> new AuthException("Department not found: " + id));

        long users = authRepository.findAll().stream()
                .filter(a -> dep.getName().equalsIgnoreCase(a.getDepartment()))
                .count();

        DepartmentResponseDto dto = DepartmentResponseDto.from(dep);
        dto.setUsers(users);
        return dto;
    }

    @Override
    public DepartmentResponseDto update(Long id, UpdateDepartmentDto dto) {

        DepartmentEntity dep = departmentRepository.findById(id)
                .orElseThrow(() -> new AuthException("Department not found: " + id));

        if (dto.getName() != null && !dto.getName().isBlank())
            dep.setName(dto.getName().trim());

        if (dto.getUnitId() != null) {
            if (dto.getUnitId() == 0) {
                dep.setUnit(null);
            } else {
                UnitEntity unit = unitRepository.findById(dto.getUnitId())
                        .orElseThrow(() -> new AuthException("Unit not found: " + dto.getUnitId()));
                dep.setUnit(unit);
            }
        }

        if (dto.getActive() != null)
            dep.setActive(dto.getActive());

        DepartmentEntity saved = departmentRepository.save(dep);

        DepartmentEntity refreshed = departmentRepository.findByIdWithUnit(saved.getId())
                .orElse(saved);

        long users = authRepository.findAll().stream()
                .filter(a -> refreshed.getName().equalsIgnoreCase(a.getDepartment()))
                .count();

        DepartmentResponseDto out = DepartmentResponseDto.from(refreshed);
        out.setUsers(users);
        return out;
    }

    @Override
    public void delete(Long id) {
        if (!departmentRepository.existsById(id))
            throw new AuthException("Department not found: " + id);
        departmentRepository.deleteById(id);
    }

    @Override
    public DepartmentStatsDto getStats() {

        long total    = departmentRepository.count();
        long active   = departmentRepository.countByActive(true);
        long inactive = departmentRepository.countByActive(false);

        List<String> departmentNames = departmentRepository.findAll().stream()
                .map(DepartmentEntity::getName)
                .toList();

        long assignedUsers = authRepository.findAll().stream()
                .filter(a -> a.getDepartment() != null &&
                        departmentNames.contains(a.getDepartment()))
                .count();

        return new DepartmentStatsDto(total, active, inactive, assignedUsers);
    }

    private String generateCode(String name) {
        String slug = name.trim().toUpperCase().replaceAll("[^A-Z0-9]+", "_");
        return "DEP_" + slug + "_" + System.currentTimeMillis();
    }
}
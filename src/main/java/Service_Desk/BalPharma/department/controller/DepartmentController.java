package Service_Desk.BalPharma.department.controller;

import Service_Desk.BalPharma.department.dto.*;
import Service_Desk.BalPharma.department.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<DepartmentResponseDto> create(@RequestBody CreateDepartmentDto dto) {
        return ResponseEntity.ok(departmentService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<DepartmentResponseDto>> getAll() {
        return ResponseEntity.ok(departmentService.getAll());
    }

    @GetMapping("/stats")
    public ResponseEntity<DepartmentStatsDto> getStats() {
        return ResponseEntity.ok(departmentService.getStats());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponseDto> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponseDto> update(@PathVariable Long id,
                                                        @RequestBody UpdateDepartmentDto dto) {
        return ResponseEntity.ok(departmentService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return ResponseEntity.ok("Department deleted");
    }
}
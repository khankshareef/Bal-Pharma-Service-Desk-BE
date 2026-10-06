package Service_Desk.BalPharma.unit.controller;

import Service_Desk.BalPharma.unit.dto.CreateUnitDto;
import Service_Desk.BalPharma.unit.dto.UpdateUnitDto;
import Service_Desk.BalPharma.unit.dto.UnitResponseDto;
import Service_Desk.BalPharma.unit.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;

    @PostMapping
    public ResponseEntity<UnitResponseDto> create(@RequestBody CreateUnitDto dto) {
        return ResponseEntity.ok(unitService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<UnitResponseDto>> getAll() {
        return ResponseEntity.ok(unitService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnitResponseDto> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(unitService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnitResponseDto> update(@PathVariable Long id,
                                                  @RequestBody UpdateUnitDto dto) {
        return ResponseEntity.ok(unitService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        unitService.delete(id);
        return ResponseEntity.ok("Unit deleted successfully");
    }
}
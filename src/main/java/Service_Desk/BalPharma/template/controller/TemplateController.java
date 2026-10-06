package Service_Desk.BalPharma.template.controller;

import Service_Desk.BalPharma.template.dto.*;
import Service_Desk.BalPharma.template.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    @PostMapping
    public ResponseEntity<TemplateResponseDto> create(@RequestBody CreateTemplateDto dto) {
        return ResponseEntity.ok(templateService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<TemplateResponseDto>> getAll() {
        return ResponseEntity.ok(templateService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TemplateResponseDto> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(templateService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TemplateResponseDto> update(@PathVariable Long id,
                                                      @RequestBody UpdateTemplateDto dto) {
        return ResponseEntity.ok(templateService.update(id, dto));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<TemplateResponseDto> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(templateService.toggleActive(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        templateService.delete(id);
        return ResponseEntity.ok("Template deleted");
    }
}
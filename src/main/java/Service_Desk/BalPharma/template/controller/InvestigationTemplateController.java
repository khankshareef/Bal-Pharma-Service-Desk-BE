package Service_Desk.BalPharma.template.controller;

import Service_Desk.BalPharma.template.dto.InvestigationTemplateDto;
import Service_Desk.BalPharma.template.service.InvestigationTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/investigation-templates")
@RequiredArgsConstructor
public class InvestigationTemplateController {

    private final InvestigationTemplateService service;

    @GetMapping("/type/{type}")
    public ResponseEntity<List<InvestigationTemplateDto>> getByType(@PathVariable String type) {
        return ResponseEntity.ok(service.getByType(type));
    }

    @PostMapping
    public ResponseEntity<InvestigationTemplateDto> create(@RequestBody InvestigationTemplateDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
package Service_Desk.BalPharma.audit.controller;

import Service_Desk.BalPharma.audit.dto.AuditLogResponseDto;
import Service_Desk.BalPharma.audit.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<AuditLogResponseDto>> getAll(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String unitCode) {

        if (type != null && !type.isBlank())
            return ResponseEntity.ok(auditLogService.getByType(type));
        if (unitCode != null && !unitCode.isBlank())
            return ResponseEntity.ok(auditLogService.getByUnit(unitCode));

        return ResponseEntity.ok(auditLogService.getAll());
    }
}
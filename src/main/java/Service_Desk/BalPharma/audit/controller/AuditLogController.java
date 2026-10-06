package Service_Desk.BalPharma.audit.controller;

import Service_Desk.BalPharma.audit.dto.AuditLogResponseDto;
import Service_Desk.BalPharma.audit.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/audit-log")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService service;

    @GetMapping
    public ResponseEntity<List<AuditLogResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
}
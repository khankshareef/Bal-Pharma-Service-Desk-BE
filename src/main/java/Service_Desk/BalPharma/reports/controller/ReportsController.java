package Service_Desk.BalPharma.reports.controller;

import Service_Desk.BalPharma.reports.dto.ReportsDto;
import Service_Desk.BalPharma.reports.service.ReportsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportsController {

    private final ReportsService service;

    @GetMapping("/dashboard")
    public ResponseEntity<ReportsDto> dashboard() {
        return ResponseEntity.ok(service.build());
    }
}
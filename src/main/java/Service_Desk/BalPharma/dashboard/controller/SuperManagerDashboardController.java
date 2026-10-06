package Service_Desk.BalPharma.dashboard.controller;

import Service_Desk.BalPharma.dashboard.dto.SuperManagerDashboardDto;
import Service_Desk.BalPharma.dashboard.service.SuperManagerDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class SuperManagerDashboardController {

    private final SuperManagerDashboardService service;

    @GetMapping("/super-manager")
    public ResponseEntity<SuperManagerDashboardDto> get() {
        return ResponseEntity.ok(service.load());
    }
}
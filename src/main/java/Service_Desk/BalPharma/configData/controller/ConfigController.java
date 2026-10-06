package Service_Desk.BalPharma.configData.controller;

import Service_Desk.BalPharma.configData.dto.PriorityConfigDto;
import Service_Desk.BalPharma.configData.dto.StatusConfigDto;
import Service_Desk.BalPharma.configData.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService service;

    @GetMapping("/priorities")
    public ResponseEntity<List<PriorityConfigDto>> priorities() {
        return ResponseEntity.ok(service.getPriorities());
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<StatusConfigDto>> statuses() {
        return ResponseEntity.ok(service.getStatuses());
    }
}
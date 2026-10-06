package Service_Desk.BalPharma.analytics.controller;

import Service_Desk.BalPharma.analytics.dto.AnalyticsDto;
import Service_Desk.BalPharma.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService service;

    @GetMapping
    public ResponseEntity<AnalyticsDto> get(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) startDate = LocalDate.now().minusDays(29);
        if (endDate == null) endDate = LocalDate.now();

        return ResponseEntity.ok(service.build(startDate, endDate));
    }
}
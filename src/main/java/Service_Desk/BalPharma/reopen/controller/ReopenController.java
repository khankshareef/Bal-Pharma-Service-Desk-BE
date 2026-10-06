package Service_Desk.BalPharma.reopen.controller;

import Service_Desk.BalPharma.reopen.dto.*;
import Service_Desk.BalPharma.reopen.service.ReopenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reopens")
@RequiredArgsConstructor
public class ReopenController {

    private final ReopenService reopenService;

    @PostMapping
    public ResponseEntity<ReopenResponseDto> request(
            @RequestBody CreateReopenDto dto,
            @RequestParam String employeeId
    ) {
        return ResponseEntity.ok(reopenService.requestReopen(dto, employeeId));
    }

    @GetMapping
    public ResponseEntity<List<ReopenResponseDto>> getAll() {
        return ResponseEntity.ok(reopenService.getAll());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ReopenResponseDto>> getPending() {
        return ResponseEntity.ok(reopenService.getPending());
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<ReopenResponseDto>> getByTicket(@PathVariable Long ticketId) {
        return ResponseEntity.ok(reopenService.getByTicket(ticketId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReopenResponseDto> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(reopenService.getById(id));
    }

    @PatchMapping("/{id}/review")
    public ResponseEntity<ReopenResponseDto> review(
            @PathVariable Long id,
            @RequestBody ReviewReopenDto dto,
            @RequestParam String reviewerId
    ) {
        return ResponseEntity.ok(reopenService.review(id, dto, reviewerId));
    }
}
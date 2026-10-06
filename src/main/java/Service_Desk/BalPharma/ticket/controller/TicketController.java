package Service_Desk.BalPharma.ticket.controller;

import Service_Desk.BalPharma.ticket.dto.*;
import Service_Desk.BalPharma.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Service_Desk.BalPharma.reopen.dto.CreateReopenDto;
import Service_Desk.BalPharma.reopen.dto.TicketReopenResponseDto;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketResponseDto> create(
            @RequestBody CreateTicketDto dto,
            @RequestParam String employeeId
    ) {
        return ResponseEntity.ok(ticketService.create(dto, employeeId));
    }

    @GetMapping
    public ResponseEntity<List<TicketResponseDto>> getAll() {
        return ResponseEntity.ok(ticketService.getAll());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<TicketResponseDto>> getByEmployee(
            @PathVariable String employeeId) {
        return ResponseEntity.ok(ticketService.getByEmployee(employeeId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDto> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponseDto> update(
            @PathVariable Long id,
            @RequestBody UpdateTicketDto dto,
            @RequestParam String employeeId
    ) {
        return ResponseEntity.ok(ticketService.update(id, dto, employeeId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponseDto> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateTicketStatusDto dto,
            @RequestParam String employeeId) {
        return ResponseEntity.ok(ticketService.updateStatus(id, dto, employeeId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        ticketService.delete(id);
        return ResponseEntity.ok("Ticket deleted");
    }

    @GetMapping("/stats")
    public ResponseEntity<TicketStatsDto> getStats() {
        return ResponseEntity.ok(ticketService.getStats());
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<TicketResponseDto> assignTicket(
            @PathVariable Long id,
            @RequestBody(required = false) AssignTicketDto dto,
            @RequestParam String assignedBy) {

        if (dto == null) dto = new AssignTicketDto();
        return ResponseEntity.ok(ticketService.assignTicket(id, dto, assignedBy));
    }

    @GetMapping("/assigned/executive/{employeeId}")
    public ResponseEntity<List<TicketResponseDto>> getAssignedTo(
            @PathVariable String employeeId) {
        return ResponseEntity.ok(ticketService.getAssignedToExecutive(employeeId));
    }

    @GetMapping("/unassigned")
    public ResponseEntity<List<TicketResponseDto>> getUnassigned() {
        return ResponseEntity.ok(ticketService.getUnassignedOpenTickets());
    }

    @GetMapping("/sla-dashboard/{employeeId}")
    public ResponseEntity<SlaDashboardDto> getSlaDashboard(
            @PathVariable String employeeId) {
        return ResponseEntity.ok(ticketService.getSlaDashboard(employeeId));
    }

    @GetMapping("/stats/executive/{employeeId}")
    public ResponseEntity<TicketStatsDto> getExecutiveStats(
            @PathVariable String employeeId) {
        return ResponseEntity.ok(ticketService.getStatsForExecutive(employeeId));
    }
    @PostMapping("/{id}/reopen-request")
    public ResponseEntity<TicketReopenResponseDto> requestReopen(
            @PathVariable Long id,
            @RequestBody CreateReopenDto dto,
            @RequestParam String employeeId) {
        return ResponseEntity.ok(ticketService.requestReopen(id, dto, employeeId));
    }
}
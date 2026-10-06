package Service_Desk.BalPharma.ticket_request.controller;

import Service_Desk.BalPharma.ticket_request.dto.CreateTicketInfoRequestDto;
import Service_Desk.BalPharma.ticket_request.dto.RespondTicketInfoRequestDto;
import Service_Desk.BalPharma.ticket_request.dto.TicketInfoRequestResponseDto;
import Service_Desk.BalPharma.ticket_request.service.TicketInfoRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ticket-requests")
@RequiredArgsConstructor
public class TicketInfoRequestController {

    private final TicketInfoRequestService service;

    @PostMapping
    public ResponseEntity<TicketInfoRequestResponseDto> create(
            @RequestBody CreateTicketInfoRequestDto dto,
            @RequestParam String requestedBy) {
        return ResponseEntity.ok(service.create(dto, requestedBy));
    }

    @GetMapping
    public ResponseEntity<List<TicketInfoRequestResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketInfoRequestResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<TicketInfoRequestResponseDto>> getByTicket(
            @PathVariable Long ticketId) {
        return ResponseEntity.ok(service.getByTicket(ticketId));
    }

    @GetMapping("/requester/{employeeId}")
    public ResponseEntity<List<TicketInfoRequestResponseDto>> getByRequester(
            @PathVariable String employeeId) {
        return ResponseEntity.ok(service.getByRequester(employeeId));
    }

    @PostMapping("/{id}/respond")
    public ResponseEntity<TicketInfoRequestResponseDto> respond(
            @PathVariable Long id,
            @RequestBody RespondTicketInfoRequestDto dto,
            @RequestParam String respondedBy) {
        return ResponseEntity.ok(service.respond(id, dto, respondedBy));
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<TicketInfoRequestResponseDto> close(
            @PathVariable Long id,
            @RequestParam String employeeId) {
        return ResponseEntity.ok(service.close(id, employeeId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pending/{employeeId}")
    public ResponseEntity<List<TicketInfoRequestResponseDto>> pending(
            @PathVariable String employeeId) {
        return ResponseEntity.ok(service.getPendingForUser(employeeId));
    }


}
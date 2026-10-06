package Service_Desk.BalPharma.reopen.service;

import Service_Desk.BalPharma.auth.entity.AuthEntity;
import Service_Desk.BalPharma.auth.repository.AuthRepository;
import Service_Desk.BalPharma.exception.AuthException;
import Service_Desk.BalPharma.reopen.dto.*;
import Service_Desk.BalPharma.reopen.entity.TicketReopenEntity;
import Service_Desk.BalPharma.reopen.repository.TicketReopenRepository;
import Service_Desk.BalPharma.ticket.entity.TicketEntity;
import Service_Desk.BalPharma.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReopenServiceImpl implements ReopenService {

    private final TicketReopenRepository reopenRepository;
    private final TicketRepository ticketRepository;
    private final AuthRepository authRepository;

    @Override
    public ReopenResponseDto requestReopen(CreateReopenDto dto, String employeeId) {

        if (dto.getTicketId() == null)
            throw new AuthException("Ticket ID is required");

        if (dto.getReason() == null || dto.getReason().isBlank())
            throw new AuthException("Reason is required");

        AuthEntity requester = authRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new AuthException("Employee not found"));

        TicketEntity ticket = ticketRepository.findByIdWithRelations(dto.getTicketId())
                .orElseThrow(() -> new AuthException("Ticket not found"));

        if (!"RESOLVED".equals(ticket.getStatus()) && !"CLOSED".equals(ticket.getStatus()))
            throw new AuthException("Only RESOLVED or CLOSED tickets can be reopened");

        if (ticket.getCreatedBy() == null ||
                !ticket.getCreatedBy().getEmployeeId().equals(employeeId))
            throw new AuthException("You can only reopen your own tickets");

        // Prevent duplicate pending requests
        if (reopenRepository.existsByTicketIdAndStatus(ticket.getId(), "PENDING"))
            throw new AuthException("A reopen request for this ticket is already pending");

        TicketReopenEntity r = new TicketReopenEntity();
        r.setTicket(ticket);
        r.setRequestedBy(requester);
        r.setReason(dto.getReason().trim());
        r.setStatus("PENDING");

        TicketReopenEntity saved = reopenRepository.save(r);

        return ReopenResponseDto.from(
                reopenRepository.findByIdWithRelations(saved.getId()).orElse(saved)
        );
    }

    @Override
    public List<ReopenResponseDto> getAll() {
        return reopenRepository.findAllWithRelations().stream()
                .map(ReopenResponseDto::from)
                .toList();
    }

    @Override
    public List<ReopenResponseDto> getPending() {
        return reopenRepository.findByStatus("PENDING").stream()
                .map(ReopenResponseDto::from)
                .toList();
    }

    @Override
    public List<ReopenResponseDto> getByTicket(Long ticketId) {
        return reopenRepository.findByTicketId(ticketId).stream()
                .map(ReopenResponseDto::from)
                .toList();
    }

    @Override
    public ReopenResponseDto getById(Long id) {
        return ReopenResponseDto.from(
                reopenRepository.findByIdWithRelations(id)
                        .orElseThrow(() -> new AuthException("Reopen request not found: " + id))
        );
    }

    @Override
    public ReopenResponseDto review(Long id, ReviewReopenDto dto, String reviewerEmployeeId) {

        if (dto.getAction() == null || dto.getAction().isBlank())
            throw new AuthException("Action is required (APPROVE or REJECT)");

        TicketReopenEntity r = reopenRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new AuthException("Reopen request not found: " + id));

        if (!"PENDING".equals(r.getStatus()))
            throw new AuthException("This request has already been reviewed");

        AuthEntity reviewer = authRepository.findByEmployeeId(reviewerEmployeeId)
                .orElseThrow(() -> new AuthException("Reviewer not found"));

        String action = dto.getAction().toUpperCase();

        r.setReviewedBy(reviewer);
        r.setReviewComments(dto.getReviewComments());
        r.setReviewedAt(LocalDateTime.now());

        if ("APPROVE".equals(action)) {
            r.setStatus("APPROVED");

            TicketEntity ticket = r.getTicket();
            ticket.setStatus("IN_PROGRESS");
            ticket.setSlaStatus("ON_TRACK");
            ticket.setResolvedAt(null);
            ticketRepository.save(ticket);

        } else if ("REJECT".equals(action)) {
            r.setStatus("REJECTED");
        } else {
            throw new AuthException("Invalid action: " + action);
        }

        reopenRepository.save(r);

        return ReopenResponseDto.from(
                reopenRepository.findByIdWithRelations(r.getId()).orElse(r)
        );
    }
}
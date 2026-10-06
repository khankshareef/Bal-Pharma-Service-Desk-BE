package Service_Desk.BalPharma.ticket_request.service;

import Service_Desk.BalPharma.auth.entity.AuthEntity;
import Service_Desk.BalPharma.auth.repository.AuthRepository;
import Service_Desk.BalPharma.exception.AuthException;
import Service_Desk.BalPharma.notification.service.NotificationService;
import Service_Desk.BalPharma.ticket.entity.TicketEntity;
import Service_Desk.BalPharma.ticket.repository.TicketRepository;
import Service_Desk.BalPharma.ticket_request.dto.CreateTicketInfoRequestDto;
import Service_Desk.BalPharma.ticket_request.dto.RespondTicketInfoRequestDto;
import Service_Desk.BalPharma.ticket_request.dto.TicketInfoRequestResponseDto;
import Service_Desk.BalPharma.ticket_request.entity.TicketInfoRequestEntity;
import Service_Desk.BalPharma.ticket_request.repository.TicketInfoRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketInfoRequestServiceImpl implements TicketInfoRequestService {

    private final TicketInfoRequestRepository requestRepository;
    private final TicketRepository ticketRepository;
    private final AuthRepository authRepository;
    private final NotificationService notificationService;

    @Override
    public TicketInfoRequestResponseDto create(CreateTicketInfoRequestDto dto, String employeeId) {

        if (dto.getTicketId() == null)
            throw new AuthException("Ticket is required");
        if (dto.getMessage() == null || dto.getMessage().isBlank())
            throw new AuthException("Message is required");

        TicketEntity ticket = ticketRepository.findByIdWithRelations(dto.getTicketId())
                .orElseThrow(() -> new AuthException("Ticket not found: " + dto.getTicketId()));

        AuthEntity requester = authRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new AuthException("Employee not found"));

        TicketInfoRequestEntity req = new TicketInfoRequestEntity();
        req.setTicket(ticket);
        req.setRequestedBy(requester);
        req.setMessage(dto.getMessage().trim());
        req.setAttachments(dto.getAttachments());
        req.setAttachmentNames(dto.getAttachmentNames());
        req.setStatus("PENDING");
        req.setResponse(null);
        req.setRespondedBy(null);
        req.setRespondedAt(null);

        TicketInfoRequestEntity saved = requestRepository.save(req);

        if (ticket.getCreatedBy() != null) {
            notificationService.notifyUser(
                    ticket.getCreatedBy().getId(),
                    "Info requested on " + ticket.getTicketCode(),
                    requester.getName() + " requested more info: " + saved.getMessage(),
                    "INFO",
                    "TICKET_INFO_REQUESTED",
                    ticket.getId()
            );
        }

        return TicketInfoRequestResponseDto.from(saved);
    }

    @Override
    public TicketInfoRequestResponseDto getById(Long id) {
        return TicketInfoRequestResponseDto.from(
                requestRepository.findByIdWithRelations(id)
                        .orElseThrow(() -> new AuthException("Request not found: " + id))
        );
    }

    @Override
    public List<TicketInfoRequestResponseDto> getAll() {
        return requestRepository.findAllWithRelations().stream()
                .map(TicketInfoRequestResponseDto::from)
                .toList();
    }

    @Override
    public List<TicketInfoRequestResponseDto> getByTicket(Long ticketId) {
        return requestRepository.findByTicketId(ticketId).stream()
                .map(TicketInfoRequestResponseDto::from)
                .toList();
    }

    @Override
    public List<TicketInfoRequestResponseDto> getPendingForUser(String employeeId) {
        AuthEntity user = authRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new AuthException("Employee not found"));

        return requestRepository.findAllWithRelations().stream()
                .filter(r -> r.getTicket() != null
                        && r.getTicket().getCreatedBy() != null
                        && r.getTicket().getCreatedBy().getId().equals(user.getId()))
                .filter(r -> !"CLOSED".equalsIgnoreCase(r.getStatus()))
                .map(TicketInfoRequestResponseDto::from)
                .toList();
    }

    @Override
    public List<TicketInfoRequestResponseDto> getByRequester(String employeeId) {
        AuthEntity user = authRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new AuthException("Employee not found"));
        return requestRepository.findByRequestedById(user.getId()).stream()
                .map(TicketInfoRequestResponseDto::from)
                .toList();
    }

    @Override
    public TicketInfoRequestResponseDto respond(Long id,
                                                RespondTicketInfoRequestDto dto,
                                                String employeeId) {

        TicketInfoRequestEntity req = requestRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new AuthException("Request not found: " + id));

        if ("CLOSED".equalsIgnoreCase(req.getStatus()))
            throw new AuthException("This request is already closed");

        if (dto.getResponse() == null || dto.getResponse().isBlank())
            throw new AuthException("Response message is required");

        AuthEntity responder = authRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new AuthException("Employee not found"));

        req.setResponse(dto.getResponse().trim());
        if (dto.getAttachments() != null) req.setAttachments(dto.getAttachments());
        if (dto.getAttachmentNames() != null) req.setAttachmentNames(dto.getAttachmentNames());
        req.setRespondedBy(responder);
        req.setRespondedAt(LocalDateTime.now());
        req.setStatus("RESPONDED");

        TicketInfoRequestEntity saved = requestRepository.save(req);

        if (saved.getRequestedBy() != null) {
            notificationService.notifyUser(
                    saved.getRequestedBy().getId(),
                    "Reply received on " + saved.getTicket().getTicketCode(),
                    responder.getName() + " replied: " + saved.getResponse(),
                    "SUCCESS",
                    "TICKET_INFO_RESPONDED",
                    saved.getTicket().getId()
            );
        }

        return TicketInfoRequestResponseDto.from(saved);
    }

    @Override
    public TicketInfoRequestResponseDto close(Long id, String employeeId) {
        TicketInfoRequestEntity req = requestRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new AuthException("Request not found: " + id));

        if (!"PENDING".equalsIgnoreCase(req.getStatus())
                && !"RESPONDED".equalsIgnoreCase(req.getStatus()))
            throw new AuthException("Cannot close a request with status: " + req.getStatus());

        req.setStatus("CLOSED");
        return TicketInfoRequestResponseDto.from(requestRepository.save(req));
    }

    @Override
    public void delete(Long id) {
        if (!requestRepository.existsById(id))
            throw new AuthException("Request not found: " + id);
        requestRepository.deleteById(id);
    }
}
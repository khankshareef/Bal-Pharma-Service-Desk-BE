package Service_Desk.BalPharma.ticket.service;

import Service_Desk.BalPharma.auth.entity.AuthEntity;
import Service_Desk.BalPharma.auth.repository.AuthRepository;
import Service_Desk.BalPharma.category.entity.CategoryEntity;
import Service_Desk.BalPharma.category.entity.SubCategoryEntity;
import Service_Desk.BalPharma.category.repository.CategoryRepository;
import Service_Desk.BalPharma.category.repository.SubCategoryRepository;
import Service_Desk.BalPharma.department.entity.DepartmentEntity;
import Service_Desk.BalPharma.department.repository.DepartmentRepository;
import Service_Desk.BalPharma.exception.AuthException;
import Service_Desk.BalPharma.location.Role;
import Service_Desk.BalPharma.notification.service.NotificationService;
import Service_Desk.BalPharma.reopen.entity.TicketReopenEntity;
import Service_Desk.BalPharma.reopen.repository.TicketReopenRepository;
import Service_Desk.BalPharma.template.entity.TicketTemplateEntity;
import Service_Desk.BalPharma.template.repository.TicketTemplateRepository;
import Service_Desk.BalPharma.ticket.dto.*;
import Service_Desk.BalPharma.ticket.entity.TicketEntity;
import Service_Desk.BalPharma.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import Service_Desk.BalPharma.reopen.dto.CreateReopenDto;
import Service_Desk.BalPharma.reopen.dto.TicketReopenResponseDto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketServiceImpl implements TicketService {

    private static final long REOPEN_WINDOW_HOURS = 24;

    private final TicketRepository ticketRepository;
    private final AuthRepository authRepository;
    private final DepartmentRepository departmentRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final TicketTemplateRepository templateRepository;
    private final TicketReopenRepository reopenRepository;
    private final NotificationService notificationService;
    private final ExecutiveAssignmentService assignmentService;


    @Override
    public TicketResponseDto create(CreateTicketDto dto, String employeeId) {

        if (dto.getSubject() == null || dto.getSubject().isBlank())
            throw new AuthException("Subject is required");
        if (dto.getPriority() == null || dto.getPriority().isBlank())
            throw new AuthException("Priority is required");
        if (dto.getDepartmentId() == null)
            throw new AuthException("Department is required");
        if (dto.getCategoryId() == null)
            throw new AuthException("Category is required");

        AuthEntity creator = authRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new AuthException("Employee not found"));

        DepartmentEntity dep = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new AuthException("Department not found"));

        CategoryEntity cat = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new AuthException("Category not found"));

        SubCategoryEntity sub = null;
        if (dto.getSubCategoryId() != null) {
            sub = subCategoryRepository.findById(dto.getSubCategoryId())
                    .orElseThrow(() -> new AuthException("Sub-category not found"));
        }

        TicketTemplateEntity template = null;
        if (dto.getTemplateId() != null) {
            template = templateRepository.findById(dto.getTemplateId())
                    .orElseThrow(() -> new AuthException("Template not found"));
        }

        TicketEntity t = new TicketEntity();
        t.setTicketCode(generateTicketCode());
        t.setUnitName(dto.getUnitName());
        t.setAddress(dto.getAddress());
        t.setCreatedBy(creator);
        t.setDepartment(dep);
        t.setCategory(cat);
        t.setSubCategory(sub);
        t.setTemplate(template);
        t.setSubject(dto.getSubject().trim());
        t.setDescription(dto.getDescription());
        t.setPriority(dto.getPriority().toUpperCase());
        t.setStatus("OPEN");
        t.setSlaStatus("ON_TRACK");

        t.setAttachmentUrl(dto.getAttachmentUrl());
        t.setAttachmentName(dto.getAttachmentName());
        t.setAttachmentUrls(dto.getAttachmentUrls());
        t.setAttachmentNames(dto.getAttachmentNames());

        TicketEntity saved = ticketRepository.save(t);

        notificationService.notifyTicketEvent(
                saved.getId(),
                "New Ticket: " + saved.getTicketCode(),
                creator.getName() + " raised a new ticket: " + saved.getSubject(),
                "INFO",
                "TICKET_CREATED",
                resolveExecutivesForTicket()
        );

        return toResponse(
                ticketRepository.findByIdWithRelations(saved.getId()).orElse(saved)
        );
    }


    @Override
    public List<TicketResponseDto> getAll() {
        return ticketRepository.findAllWithRelations().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<TicketResponseDto> getByEmployee(String employeeId) {
        AuthEntity user = authRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new AuthException("Employee not found"));
        return ticketRepository.findByCreatedById(user.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public TicketResponseDto getById(Long id) {
        return toResponse(
                ticketRepository.findByIdWithRelations(id)
                        .orElseThrow(() -> new AuthException("Ticket not found: " + id))
        );
    }


    @Override
    public TicketResponseDto update(Long id, UpdateTicketDto dto, String employeeId) {

        TicketEntity t = ticketRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new AuthException("Ticket not found: " + id));

        if (t.getCreatedBy() == null ||
                !t.getCreatedBy().getEmployeeId().equals(employeeId))
            throw new AuthException("You are not allowed to edit this ticket");

        if (dto.getUnitName() != null) t.setUnitName(dto.getUnitName());
        if (dto.getAddress() != null)  t.setAddress(dto.getAddress());

        if (dto.getSubject() != null && !dto.getSubject().isBlank())
            t.setSubject(dto.getSubject().trim());

        if (dto.getDescription() != null)
            t.setDescription(dto.getDescription());

        if (dto.getPriority() != null && !dto.getPriority().isBlank())
            t.setPriority(dto.getPriority().toUpperCase());

        if (dto.getDepartmentId() != null)
            t.setDepartment(departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new AuthException("Department not found")));

        if (dto.getCategoryId() != null)
            t.setCategory(categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new AuthException("Category not found")));

        if (dto.getSubCategoryId() != null)
            t.setSubCategory(subCategoryRepository.findById(dto.getSubCategoryId())
                    .orElseThrow(() -> new AuthException("Sub-category not found")));

        if (dto.getAttachmentUrl() != null) t.setAttachmentUrl(dto.getAttachmentUrl());
        if (dto.getAttachmentName() != null) t.setAttachmentName(dto.getAttachmentName());
        if (dto.getAttachmentUrls() != null) t.setAttachmentUrls(dto.getAttachmentUrls());
        if (dto.getAttachmentNames() != null) t.setAttachmentNames(dto.getAttachmentNames());

        TicketEntity saved = ticketRepository.save(t);

        notificationService.notifyTicketEvent(
                saved.getId(),
                "Ticket " + saved.getTicketCode() + " updated",
                "The ticket was updated by " + employeeId,
                "INFO",
                "TICKET_UPDATED",
                resolveExecutivesForTicket()
        );

        return toResponse(
                ticketRepository.findByIdWithRelations(saved.getId()).orElse(saved)
        );
    }


    @Override
    public TicketResponseDto updateStatus(Long id, UpdateTicketStatusDto dto, String employeeId) {

        TicketEntity t = ticketRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new AuthException("Ticket not found: " + id));

        AuthEntity actor = authRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new AuthException("Employee not found"));

        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            String requested = dto.getStatus().trim().toUpperCase();
            String current = t.getStatus() == null ? "" : t.getStatus().toUpperCase();

            /* guard: cannot close while reopen pending */
            if (("RESOLVED".equals(requested) || "CLOSED".equals(requested))
                    && reopenRepository.existsByTicketIdAndStatus(id, "PENDING")) {
                throw new AuthException(
                        "Cannot mark ticket as " + requested +
                                " while a reopen request is pending.");
            }

            /* RESOLVE: only assigned executive or manager */
            if ("RESOLVED".equals(requested)) {
                if (!"IN_PROGRESS".equals(current) && !"OPEN".equals(current)) {
                    throw new AuthException(
                            "Only OPEN or IN_PROGRESS tickets can be resolved. Current: " + current);
                }

                boolean isAssignee = t.getAssignedTo() != null
                        && t.getAssignedTo().getId().equals(actor.getId());
                boolean isManager = actor.hasAnyRole(
                        Role.SUPER_MANAGER, Role.DEPUTY_MANAGER, Role.ADMIN);

                if (!isAssignee && !isManager) {
                    throw new AuthException("Only the assigned executive can resolve this ticket.");
                }

                t.setStatus("RESOLVED");
                if (t.getResolvedAt() == null) t.setResolvedAt(LocalDateTime.now());
            }

            /* CLOSE: only the ticket creator */
            else if ("CLOSED".equals(requested)) {
                if (!"RESOLVED".equals(current)) {
                    throw new AuthException(
                            "Only RESOLVED tickets can be closed. Current: " + current);
                }

                boolean isCreator = t.getCreatedBy() != null
                        && t.getCreatedBy().getId().equals(actor.getId());

                boolean isManager = actor.hasAnyRole(Role.SUPER_MANAGER, Role.ADMIN);

                if (!isCreator && !isManager) {
                    throw new AuthException("Only the ticket creator can close this ticket.");
                }

                t.setStatus("CLOSED");
                if (t.getClosedAt() == null) t.setClosedAt(LocalDateTime.now());
            }

            else {
                t.setStatus(requested);
            }

            if (t.getCreatedBy() != null) {
                String msg = switch (requested) {
                    case "IN_PROGRESS" -> "Your ticket is now being investigated.";
                    case "RESOLVED"    -> "Your ticket has been resolved. Please confirm to close it.";
                    case "CLOSED"      -> "Your ticket has been closed.";
                    default            -> "Ticket status changed to " + requested;
                };

                notificationService.notifyUser(
                        t.getCreatedBy().getId(),
                        "Ticket " + t.getTicketCode() + " — " + requested,
                        msg,
                        "RESOLVED".equals(requested) ? "SUCCESS" : "INFO",
                        "TICKET_" + requested,
                        t.getId()
                );
            }
        }

        if (dto.getSlaStatus() != null && !dto.getSlaStatus().isBlank())
            t.setSlaStatus(dto.getSlaStatus().trim().toUpperCase());

        if (dto.getResolutionNotes() != null)
            t.setResolutionNotes(dto.getResolutionNotes());

        if (dto.getResolutionType() != null && !dto.getResolutionType().isBlank())
            t.setResolutionType(dto.getResolutionType().trim().toUpperCase());

        TicketEntity saved = ticketRepository.save(t);

        return toResponse(
                ticketRepository.findByIdWithRelations(saved.getId()).orElse(saved)
        );
    }


    @Override
    public TicketResponseDto assignTicket(Long id, AssignTicketDto dto, String assignedByEmployeeId) {

        TicketEntity t = ticketRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new AuthException("Ticket not found: " + id));

        if (!"OPEN".equalsIgnoreCase(t.getStatus())) {
            throw new AuthException(
                    "Only OPEN tickets can be assigned. Current status: " + t.getStatus());
        }

        if (t.getAssignedTo() != null) {
            throw new AuthException(
                    "Ticket is already assigned to " + t.getAssignedTo().getEmployeeId());
        }

        AuthEntity assigner = authRepository.findByEmployeeId(assignedByEmployeeId)
                .orElseThrow(() -> new AuthException("Assigner not found"));

        AuthEntity assigned;

        if (dto.getExecutiveId() != null) {
            assigned = assignmentService.assignTicketToSpecific(t, dto.getExecutiveId(), assigner);
        } else {
            assigned = assignmentService.autoAssignTicket(t)
                    .orElseThrow(() -> new AuthException(
                            "No available executive for unit: "
                                    + (t.getUnitName() != null ? t.getUnitName() : "unknown")
                    ));
        }

        t.setStatus("IN_PROGRESS");
        t.setSlaStatus("ON_TRACK");

        TicketEntity saved = ticketRepository.save(t);

        notificationService.notifyUser(
                assigned.getId(),
                "Ticket Assigned: " + saved.getTicketCode(),
                "You have been assigned ticket " + saved.getTicketCode()
                        + ": " + saved.getSubject(),
                "INFO",
                "TICKET_ASSIGNED",
                saved.getId()
        );

        if (saved.getCreatedBy() != null) {
            notificationService.notifyUser(
                    saved.getCreatedBy().getId(),
                    "Ticket " + saved.getTicketCode() + " is now In Progress",
                    "Your ticket has been assigned to "
                            + assigned.getName() + " (" + assigned.getEmployeeId() + ").",
                    "INFO",
                    "TICKET_ASSIGNED",
                    saved.getId()
            );
        }

        return toResponse(
                ticketRepository.findByIdWithRelations(saved.getId()).orElse(saved)
        );
    }


    @Override
    public TicketReopenResponseDto requestReopen(
            Long ticketId,
            CreateReopenDto dto,
            String employeeId) {

        TicketEntity t = ticketRepository.findByIdWithRelations(ticketId)
                .orElseThrow(() -> new AuthException("Ticket not found: " + ticketId));

        AuthEntity requester = authRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new AuthException("Employee not found"));

        /* only RESOLVED or CLOSED tickets can be reopened */
        String status = t.getStatus() == null ? "" : t.getStatus().toUpperCase();
        if (!"RESOLVED".equals(status) && !"CLOSED".equals(status)) {
            throw new AuthException(
                    "Only RESOLVED or CLOSED tickets can be reopened. Current: " + status);
        }

        /* only the ticket creator */
        if (t.getCreatedBy() == null ||
                !t.getCreatedBy().getId().equals(requester.getId())) {
            throw new AuthException("Only the ticket creator can request a reopen.");
        }

        /* enforce the 24-hour window */
        LocalDateTime anchor = "CLOSED".equals(status)
                ? t.getClosedAt()
                : t.getResolvedAt();
        if (anchor == null) anchor = t.getUpdatedAt();
        if (anchor == null) anchor = t.getCreatedAt();

        long hoursSince = Duration.between(anchor, LocalDateTime.now()).toHours();
        if (hoursSince >= REOPEN_WINDOW_HOURS) {
            throw new AuthException(
                    "Reopen window closed. You can only reopen within "
                            + REOPEN_WINDOW_HOURS + " hours of resolution/closure.");
        }

        /* prevent duplicate PENDING reopen */
        if (reopenRepository.existsByTicketIdAndStatus(ticketId, "PENDING")) {
            throw new AuthException("A reopen request is already pending for this ticket.");
        }

        /* create the reopen record */
        TicketReopenEntity r = new TicketReopenEntity();
        r.setTicket(t);
        r.setRequestedBy(requester);
        r.setReason(dto.getReason());
        r.setStatus("PENDING");

        TicketReopenEntity saved = reopenRepository.save(r);

        notificationService.notifyTicketEvent(
                t.getId(),
                "Reopen requested for " + t.getTicketCode(),
                requester.getName() + " wants to reopen: " + dto.getReason(),
                "INFO",
                "TICKET_REOPEN_REQUESTED",
                resolveExecutivesForTicket()
        );

        return TicketReopenResponseDto.from(saved);
    }


    @Override
    public List<TicketResponseDto> getAssignedToExecutive(String employeeId) {
        AuthEntity exec = authRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new AuthException("Executive not found"));

        return ticketRepository
                .findByAssignedToId(exec.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<TicketResponseDto> getUnassignedOpenTickets() {
        return ticketRepository.findUnassignedOpenTickets().stream()
                .map(this::toResponse)
                .toList();
    }


    @Override
    public void delete(Long id) {
        if (!ticketRepository.existsById(id))
            throw new AuthException("Ticket not found: " + id);
        ticketRepository.deleteById(id);
    }


    @Override
    public SlaDashboardDto getSlaDashboard(String employeeId) {

        AuthEntity exec = authRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new AuthException("Executive not found"));

        long onTrack  = ticketRepository
                .countByAssignedToIdAndSlaStatus(exec.getId(), "ON_TRACK");
        long atRisk   = ticketRepository
                .countByAssignedToIdAndSlaStatus(exec.getId(), "AT_RISK");
        long breached = ticketRepository
                .countByAssignedToIdAndSlaStatus(exec.getId(), "BREACHED");
        long total    = ticketRepository.countByAssignedToId(exec.getId());

        double compliance = total == 0
                ? 0.0
                : ((double) onTrack / total) * 100.0;

        return new SlaDashboardDto(
                onTrack,
                atRisk,
                breached,
                total,
                Math.round(compliance * 10.0) / 10.0
        );
    }

    @Override
    public TicketStatsDto getStatsForExecutive(String employeeId) {

        AuthEntity exec = authRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new AuthException("Executive not found"));

        long id = exec.getId();

        long total      = ticketRepository.countByAssignedToId(id);
        long open       = ticketRepository.countByAssignedToIdAndStatus(id, "OPEN");
        long inProgress = ticketRepository.countByAssignedToIdAndStatus(id, "IN_PROGRESS");
        long resolved   = ticketRepository.countByAssignedToIdAndStatus(id, "RESOLVED");
        long closed     = ticketRepository.countByAssignedToIdAndStatus(id, "CLOSED");
        long onTrack    = ticketRepository.countByAssignedToIdAndSlaStatus(id, "ON_TRACK");
        long breached   = ticketRepository.countByAssignedToIdAndSlaStatus(id, "BREACHED");
        long atRisk     = ticketRepository.countByAssignedToIdAndSlaStatus(id, "AT_RISK");

        return new TicketStatsDto(
                total, open, inProgress, resolved, closed,
                onTrack, breached, atRisk
        );
    }

    @Override
    public TicketStatsDto getStats() {
        long total      = ticketRepository.count();
        long open       = ticketRepository.countByStatus("OPEN");
        long inProgress = ticketRepository.countByStatus("IN_PROGRESS");
        long resolved   = ticketRepository.countByStatus("RESOLVED");
        long closed     = ticketRepository.countByStatus("CLOSED");
        long onTrack    = ticketRepository.countBySlaStatus("ON_TRACK");
        long breached   = ticketRepository.countBySlaStatus("BREACHED");
        long atRisk     = ticketRepository.countBySlaStatus("AT_RISK");

        return new TicketStatsDto(
                total, open, inProgress, resolved, closed,
                onTrack, breached, atRisk
        );
    }


    private List<Long> resolveExecutivesForTicket() {
        List<AuthEntity> all = authRepository.findAll();
        List<Long> execIds = all.stream()
                .filter(u -> u.hasAnyRole(Role.EXECUTIVE, Role.SUPER_MANAGER))
                .map(AuthEntity::getId)
                .toList();

        System.out.println(">>> Total users: " + all.size());
        System.out.println(">>> Executives: " + execIds);
        return execIds;
    }

    private TicketResponseDto toResponse(TicketEntity t) {
        TicketResponseDto dto = TicketResponseDto.from(t);

        List<TicketReopenEntity> reopens = reopenRepository.findByTicketId(t.getId());
        if (reopens != null && !reopens.isEmpty()) {
            TicketReopenEntity latest = reopens.get(0);
            dto.setReopenId(latest.getId());
            dto.setReopenStatus(latest.getStatus());
            dto.setReopenReason(latest.getReason());
            dto.setReopenRequestedByName(
                    latest.getRequestedBy() != null
                            ? latest.getRequestedBy().getName() : null);
            dto.setReopenRequestedAt(
                    latest.getCreatedAt() != null
                            ? latest.getCreatedAt().toString() : null);
            dto.setReopenReviewComments(latest.getReviewComments());
            dto.setReopenReviewedByName(
                    latest.getReviewedBy() != null
                            ? latest.getReviewedBy().getName() : null);
        }

        return dto;
    }

    private String generateTicketCode() {
        long count = ticketRepository.count() + 1;
        return String.format("TKT-%04d", count);
    }
}
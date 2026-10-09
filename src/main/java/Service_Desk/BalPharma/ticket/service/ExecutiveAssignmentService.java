package Service_Desk.BalPharma.ticket.service;

import Service_Desk.BalPharma.auth.entity.AuthEntity;
import Service_Desk.BalPharma.auth.entity.UnitAssignment;
import Service_Desk.BalPharma.auth.repository.AuthRepository;
import Service_Desk.BalPharma.exception.AuthException;
import Service_Desk.BalPharma.location.AccountStatus;
import Service_Desk.BalPharma.location.Role;
import Service_Desk.BalPharma.ticket.entity.TicketEntity;
import Service_Desk.BalPharma.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class ExecutiveAssignmentService {

    private static final Logger log =
            LoggerFactory.getLogger(ExecutiveAssignmentService.class);

    private final AuthRepository authRepository;
    private final TicketRepository ticketRepository;

    private static final List<String> ACTIVE_STATUSES =
            List.of("OPEN", "IN_PROGRESS");

    private final Map<String, AtomicInteger> rrCounters = new ConcurrentHashMap<>();

    private String normalize(String value) {
        if (value == null) return null;
        return value.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    private boolean matches(String ticketValue, String assignmentValue) {
        if (ticketValue == null || assignmentValue == null) return false;
        return normalize(ticketValue).equals(normalize(assignmentValue));
    }

    private boolean coversUnit(AuthEntity executive, TicketEntity ticket) {
        String ticketUnitName = ticket.getUnitName();
        String ticketAddress = ticket.getAddress();

        if ((ticketUnitName == null || ticketUnitName.isBlank())
                && (ticketAddress == null || ticketAddress.isBlank())) {
            return true;
        }

        Set<UnitAssignment> locations = executive.getAllowedLocations();
        if (locations == null || locations.isEmpty()) {
            return true;
        }

        return locations.stream().anyMatch(assignment -> {
            String assignmentUnitName = assignment.getUnitName();
            String assignmentUnitCode = assignment.getUnitCode();
            String assignmentAddress = assignment.getAddress();

            boolean unitNameMatch = matches(ticketUnitName, assignmentUnitName);
            boolean unitCodeMatch = matches(ticketUnitName, assignmentUnitCode);
            boolean addressMatch = matches(ticketAddress, assignmentAddress);

            return unitNameMatch || unitCodeMatch || addressMatch;
        });
    }

    private long currentLoad(AuthEntity executive) {
        return ticketRepository.countByAssignedToIdAndStatusIn(
                executive.getId(),
                ACTIVE_STATUSES
        );
    }

    private List<AuthEntity> candidatesForDepartment(
            TicketEntity ticket,
            boolean applyUnitFilter
    ) {
        String department = ticket.getDepartment() != null
                ? ticket.getDepartment().getName()
                : null;

        List<AuthEntity> pool = authRepository.findAll().stream()
                .filter(u -> u.hasAnyRole(Role.EXECUTIVE))
                .filter(u -> u.getStatus() == AccountStatus.ACTIVE)
                .filter(u -> {
                    if (department == null || department.isBlank()) {
                        // no department context — allow any executive
                        return true;
                    }
                    return u.getDepartment() != null
                            && department.equalsIgnoreCase(u.getDepartment());
                })
                .filter(u -> !applyUnitFilter || coversUnit(u, ticket))
                .sorted(Comparator.comparing(AuthEntity::getId))
                .toList();

        // If unit filtering killed all candidates, fall back to department-only
        if (pool.isEmpty() && applyUnitFilter) {
            log.warn(
                    ">>> AutoAssign: no dept+unit match, falling back to department-only | dept={}",
                    department
            );
            return candidatesForDepartment(ticket, false);
        }

        return pool;
    }

    public Optional<AuthEntity> autoAssignTicket(TicketEntity ticket) {

        String department = ticket.getDepartment() != null
                ? ticket.getDepartment().getName()
                : null;

        List<AuthEntity> pool = candidatesForDepartment(ticket, true);

        log.info(
                ">>> AutoAssign pool for ticket={} dept={} → {}",
                ticket.getTicketCode(),
                department,
                pool.stream().map(AuthEntity::getEmployeeId).toList()
        );

        if (pool.isEmpty()) {
            log.warn(
                    ">>> AutoAssign FAILED: no executive in dept={} | ticket={}",
                    department,
                    ticket.getTicketCode()
            );
            return Optional.empty();
        }

        String key = department == null ? "__none__" : department.toLowerCase();
        AtomicInteger counter = rrCounters.computeIfAbsent(
                key,
                k -> new AtomicInteger(0)
        );

        int index = Math.floorMod(counter.getAndIncrement(), pool.size());
        AuthEntity picked = pool.get(index);

        ticket.setAssignedTo(picked);
        ticket.setAssignedAt(LocalDateTime.now());
        ticket.setAssignedBy(null);
        ticket.setAutoAssigned(true);

        log.info(
                ">>> AutoAssign OK (RR index={}): {} → {} ({}) load={}",
                index,
                ticket.getTicketCode(),
                picked.getName(),
                picked.getEmployeeId(),
                currentLoad(picked)
        );

        return Optional.of(picked);
    }

    public AuthEntity assignTicketToSpecific(
            TicketEntity ticket,
            Long executiveId,
            AuthEntity assignedBy
    ) {
        AuthEntity executive = authRepository.findById(executiveId)
                .orElseThrow(() ->
                        new AuthException("Executive not found: " + executiveId));

        if (!executive.hasRole(Role.EXECUTIVE)) {
            throw new AuthException("Selected user is not an executive");
        }

        if (executive.getStatus() != AccountStatus.ACTIVE) {
            throw new AuthException("Executive account is not active");
        }

        String ticketDept = ticket.getDepartment() != null
                ? ticket.getDepartment().getName()
                : null;
        boolean isSuper = assignedBy.hasAnyRole(
                Role.SUPER_MANAGER, Role.ADMIN);

        if (!isSuper
                && ticketDept != null
                && (executive.getDepartment() == null
                || !ticketDept.equalsIgnoreCase(executive.getDepartment()))) {
            throw new AuthException(
                    "Executive " + executive.getEmployeeId()
                            + " is not in the ticket's department (" + ticketDept + ")");
        }

        if (!coversUnit(executive, ticket)) {
            throw new AuthException(
                    "Executive " + executive.getEmployeeId()
                            + " does not cover unit '" + ticket.getUnitName() + "'");
        }

        ticket.setAssignedTo(executive);
        ticket.setAssignedAt(LocalDateTime.now());
        ticket.setAssignedBy(assignedBy);
        ticket.setAutoAssigned(false);

        return executive;
    }
}
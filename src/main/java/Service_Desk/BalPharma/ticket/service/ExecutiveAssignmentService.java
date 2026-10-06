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
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExecutiveAssignmentService {

    private static final Logger log =
            LoggerFactory.getLogger(ExecutiveAssignmentService.class);

    private final AuthRepository authRepository;
    private final TicketRepository ticketRepository;

    private static final List<String> ACTIVE_STATUSES =
            List.of("OPEN", "IN_PROGRESS");

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        return value
                .trim()
                .replaceAll("\\s+", " ")
                .toLowerCase();
    }

    private boolean matches(String ticketValue, String assignmentValue) {
        if (ticketValue == null || assignmentValue == null) {
            return false;
        }

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

            boolean unitNameMatch =
                    matches(ticketUnitName, assignmentUnitName);

            boolean unitCodeMatch =
                    matches(ticketUnitName, assignmentUnitCode);

            boolean addressMatch =
                    matches(ticketAddress, assignmentAddress);

            return unitNameMatch || unitCodeMatch || addressMatch;
        });
    }

    private long currentLoad(AuthEntity executive) {
        return ticketRepository.countByAssignedToIdAndStatusIn(
                executive.getId(),
                ACTIVE_STATUSES
        );
    }

    public Optional<AuthEntity> findBestExecutiveForUnit(TicketEntity ticket) {

        List<AuthEntity> allUsers = authRepository.findAll();

        List<AuthEntity> executives = allUsers.stream()
                .filter(u -> u.hasRole(Role.EXECUTIVE))
                .toList();

        log.info(
                ">>> AutoAssign: looking for unit='{}', address='{}'",
                ticket.getUnitName(),
                ticket.getAddress()
        );

        log.info(
                ">>> Total users: {} | EXECUTIVEs: {}",
                allUsers.size(),
                executives.size()
        );

        List<AuthEntity> activeExecutives = executives.stream()
                .filter(u -> u.getStatus() == AccountStatus.ACTIVE)
                .toList();

        for (AuthEntity executive : activeExecutives) {

            Set<UnitAssignment> units =
                    executive.getAllowedLocations();

            String summary =
                    (units == null || units.isEmpty())
                            ? "ALL units (no restriction)"
                            : units.stream()
                            .map(unit ->
                                    unit.getUnitName()
                                            + "("
                                            + unit.getUnitCode()
                                            + ")"
                            )
                            .reduce((x, y) -> x + ", " + y)
                            .orElse("-");

            log.info(
                    ">>> exec {} | units: {}",
                    executive.getEmployeeId(),
                    summary
            );
        }

        List<AuthEntity> candidates = activeExecutives.stream()
                .filter(executive -> coversUnit(executive, ticket))
                .toList();

        log.info(
                ">>> Eligible executives for unit '{}' address '{}': {} -> {}",
                ticket.getUnitName(),
                ticket.getAddress(),
                candidates.size(),
                candidates.stream()
                        .map(AuthEntity::getEmployeeId)
                        .toList()
        );

        if (candidates.isEmpty()) {

            log.warn(
                    ">>> No eligible executive found | unit={} | address={}",
                    ticket.getUnitName(),
                    ticket.getAddress()
            );

            return Optional.empty();
        }

        candidates.forEach(candidate ->
                log.info(
                        ">>> candidate {} | load={}",
                        candidate.getEmployeeId(),
                        currentLoad(candidate)
                )
        );

        return candidates.stream()
                .min(
                        Comparator
                                .comparingLong(this::currentLoad)
                                .thenComparing(AuthEntity::getEmployeeId)
                );
    }

    public Optional<AuthEntity> autoAssignTicket(TicketEntity ticket) {

        String unit = ticket.getUnitName();

        String department =
                ticket.getDepartment() != null
                        ? ticket.getDepartment().getName()
                        : null;

        Optional<AuthEntity> best =
                findBestExecutiveForUnit(ticket);

        if (best.isEmpty()) {

            log.warn(
                    ">>> AutoAssign FAILED: ticket={} | unit={} | address={} | department={}",
                    ticket.getTicketCode(),
                    unit,
                    ticket.getAddress(),
                    department
            );

            return Optional.empty();
        }

        AuthEntity executive = best.get();

        ticket.setAssignedTo(executive);
        ticket.setAssignedAt(LocalDateTime.now());
        ticket.setAssignedBy(null);
        ticket.setAutoAssigned(true);

        log.info(
                ">>> AutoAssign OK: {} | unit={} | address={} | department={} -> {} ({}) load={}",
                ticket.getTicketCode(),
                unit,
                ticket.getAddress(),
                department,
                executive.getName(),
                executive.getEmployeeId(),
                currentLoad(executive)
        );

        return Optional.of(executive);
    }

    public AuthEntity assignTicketToSpecific(
            TicketEntity ticket,
            Long executiveId,
            AuthEntity assignedBy
    ) {

        AuthEntity executive =
                authRepository.findById(executiveId)
                        .orElseThrow(() ->
                                new AuthException(
                                        "Executive not found: " + executiveId
                                )
                        );

        if (!executive.hasRole(Role.EXECUTIVE)) {
            throw new AuthException(
                    "Selected user is not an executive"
            );
        }

        if (executive.getStatus() != AccountStatus.ACTIVE) {
            throw new AuthException(
                    "Executive account is not active"
            );
        }

        if (!coversUnit(executive, ticket)) {
            throw new AuthException(
                    "Executive "
                            + executive.getEmployeeId()
                            + " does not cover unit '"
                            + ticket.getUnitName()
                            + "'"
            );
        }

        ticket.setAssignedTo(executive);
        ticket.setAssignedAt(LocalDateTime.now());
        ticket.setAssignedBy(assignedBy);
        ticket.setAutoAssigned(false);

        return executive;
    }
}
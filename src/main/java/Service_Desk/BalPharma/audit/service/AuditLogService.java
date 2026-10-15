package Service_Desk.BalPharma.audit.service;

import Service_Desk.BalPharma.audit.dto.AuditLogResponseDto;
import Service_Desk.BalPharma.audit.entity.AuditLogEntity;
import Service_Desk.BalPharma.audit.repository.AuditLogRepository;
import Service_Desk.BalPharma.auth.entity.AuthEntity;
import Service_Desk.BalPharma.ticket.entity.TicketEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");


    @Transactional(readOnly = true)
    public List<AuditLogResponseDto> getAll() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponseDto> getByType(String type) {
        return auditLogRepository.findByTypeOrderByCreatedAtDesc(type)
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponseDto> getByUnit(String unitCode) {
        return auditLogRepository.findByUnitCodeOrderByCreatedAtDesc(unitCode)
                .stream().map(this::toDto).toList();
    }


    @Transactional
    public void record(String actor,
                       String action,
                       String type,
                       String unit,
                       String details) {
        record(actor, action, type, unit, null, details);
    }

    @Transactional
    public void record(String actor,
                       String action,
                       String type,
                       String unit,
                       String unitCode,
                       String details) {

        AuditLogEntity e = new AuditLogEntity();
        e.setActor(actor != null ? actor : "system");
        e.setAction(action);
        e.setType(type);
        e.setUnit(unit != null && !unit.isBlank() ? unit : "All");
        e.setUnitCode(unitCode);
        e.setDetails(details);
        e.setCreatedAt(LocalDateTime.now());
        auditLogRepository.save(e);
    }

    @Transactional
    public void recordForUser(AuthEntity actor,
                              String action,
                              String type,
                              String details) {
        String actorLabel = actor == null
                ? "system"
                : actor.getName() + " (" + shortRole(actor) + ")";
        record(actorLabel, action, type, "All", null, details);
    }

    @Transactional
    public void recordForTicket(AuthEntity actor,
                                TicketEntity ticket,
                                String action,
                                String type,
                                String details) {

        String actorLabel = actor == null
                ? "system"
                : actor.getName() + " (" + shortRole(actor) + ")";

        String unitLabel = ticket != null && ticket.getUnitName() != null
                ? ticket.getUnitName()
                : "All";

        record(actorLabel, action, type, unitLabel, null, details);
    }


    private AuditLogResponseDto toDto(AuditLogEntity e) {
        AuditLogResponseDto d = new AuditLogResponseDto();
        d.setId(e.getId());
        d.setTimestamp(e.getCreatedAt() != null
                ? e.getCreatedAt().format(FMT) : "");
        d.setUser(e.getActor());
        d.setAction(e.getAction());
        d.setType(e.getType());
        d.setUnit(e.getUnit());
        d.setDetails(e.getDetails());
        return d;
    }

    private String shortRole(AuthEntity u) {
        if (u == null || u.getRoles() == null || u.getRoles().isEmpty()) return "?";
        return u.getRoles().iterator().next().name()
                .replace("SUPER_MANAGER", "SM")
                .replace("DEPUTY_MANAGER", "DM")
                .replace("EXECUTIVE", "EX")
                .replace("EMPLOYEE", "EMP")
                .replace("ADMIN", "ADMIN");
    }
}
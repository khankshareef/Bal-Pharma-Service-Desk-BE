package Service_Desk.BalPharma.audit.service;

import Service_Desk.BalPharma.audit.dto.AuditLogResponseDto;
import Service_Desk.BalPharma.audit.entity.AuditLogEntity;
import Service_Desk.BalPharma.audit.repository.AuditLogRepository;
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

    @Transactional
    public void record(String actor, String action, String type,
                       String unit, String details) {
        AuditLogEntity e = new AuditLogEntity();
        e.setActor(actor);
        e.setAction(action);
        e.setType(type);
        e.setUnit(unit);
        e.setDetails(details);
        e.setCreatedAt(LocalDateTime.now());
        auditLogRepository.save(e);
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
}
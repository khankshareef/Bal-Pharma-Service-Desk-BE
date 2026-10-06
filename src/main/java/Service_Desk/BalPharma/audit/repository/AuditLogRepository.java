package Service_Desk.BalPharma.audit.repository;

import Service_Desk.BalPharma.audit.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {

    List<AuditLogEntity> findAllByOrderByCreatedAtDesc();

    List<AuditLogEntity> findByTypeOrderByCreatedAtDesc(String type);

    List<AuditLogEntity> findByUnitOrderByCreatedAtDesc(String unit);
}
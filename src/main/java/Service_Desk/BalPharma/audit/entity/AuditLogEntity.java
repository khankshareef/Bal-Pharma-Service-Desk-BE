package Service_Desk.BalPharma.audit.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "audit_log", indexes = {
        @Index(name = "idx_audit_created", columnList = "created_at"),
        @Index(name = "idx_audit_type",    columnList = "type"),
        @Index(name = "idx_audit_actor",   columnList = "actor")
})
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "actor", length = 120)
    private String actor;

    @Column(name = "action", length = 120)
    private String action;

    @Column(name = "type", length = 40)
    private String type;

    @Column(name = "unit", length = 120)
    private String unit;

    @Column(name = "unit_code", length = 40)
    private String unitCode;

    @Column(name = "details", length = 1000)
    private String details;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
package Service_Desk.BalPharma.audit.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditLogResponseDto {
    private Long id;
    private String timestamp;
    private String user;
    private String action;
    private String type;
    private String unit;
    private String details;
}
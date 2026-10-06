package Service_Desk.BalPharma.notification.dto;

import Service_Desk.BalPharma.notification.entity.NotificationEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponseDto {

    private Long id;
    private String title;
    private String message;
    private String type;
    private String action;
    private Boolean isRead;
    private LocalDateTime createdAt;

    private Long ticketId;
    private String ticketCode;
    private String ticketStatus;

    public static NotificationResponseDto from(NotificationEntity n) {
        return new NotificationResponseDto(
                n.getId(),
                n.getTitle(),
                n.getMessage(),
                n.getType(),
                n.getAction(),
                n.getIsRead(),
                n.getCreatedAt(),
                n.getTicket() != null ? n.getTicket().getId() : null,
                n.getTicket() != null ? n.getTicket().getTicketCode() : null,
                n.getTicket() != null ? n.getTicket().getStatus() : null
        );
    }
}
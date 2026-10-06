package Service_Desk.BalPharma.ticket_request.dto;

import Service_Desk.BalPharma.ticket_request.entity.TicketInfoRequestEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TicketInfoRequestResponseDto {
    private Long id;
    private Long ticketId;
    private String ticketCode;
    private Long requestedById;
    private String requestedByName;
    private Long respondedById;
    private String respondedByName;
    private String message;
    private String attachments;
    private String attachmentNames;
    private String status;
    private String response;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
    private LocalDateTime updatedAt;

    public static TicketInfoRequestResponseDto from(TicketInfoRequestEntity e) {
        TicketInfoRequestResponseDto dto = new TicketInfoRequestResponseDto();
        dto.setId(e.getId());
        if (e.getTicket() != null) {
            dto.setTicketId(e.getTicket().getId());
            dto.setTicketCode(e.getTicket().getTicketCode());
        }
        if (e.getRequestedBy() != null) {
            dto.setRequestedById(e.getRequestedBy().getId());
            dto.setRequestedByName(e.getRequestedBy().getName());
        }
        if (e.getRespondedBy() != null) {
            dto.setRespondedById(e.getRespondedBy().getId());
            dto.setRespondedByName(e.getRespondedBy().getName());
        }
        dto.setMessage(e.getMessage());
        dto.setAttachments(e.getAttachments());
        dto.setAttachmentNames(e.getAttachmentNames());
        dto.setStatus(e.getStatus());
        dto.setResponse(e.getResponse());
        dto.setCreatedAt(e.getCreatedAt());
        dto.setRespondedAt(e.getRespondedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        return dto;
    }
}
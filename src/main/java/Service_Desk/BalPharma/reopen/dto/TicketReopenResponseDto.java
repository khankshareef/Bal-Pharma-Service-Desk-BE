package Service_Desk.BalPharma.reopen.dto;

import Service_Desk.BalPharma.reopen.entity.TicketReopenEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TicketReopenResponseDto {
    private Long id;
    private Long ticketId;
    private String ticketCode;
    private Long requestedById;
    private String requestedByName;
    private String reason;
    private String status;
    private String reviewComments;
    private String reviewedByName;
    private LocalDateTime createdAt;

    public static TicketReopenResponseDto from(TicketReopenEntity r) {
        TicketReopenResponseDto dto = new TicketReopenResponseDto();
        dto.setId(r.getId());

        if (r.getTicket() != null) {
            dto.setTicketId(r.getTicket().getId());
            dto.setTicketCode(r.getTicket().getTicketCode());
        }
        if (r.getRequestedBy() != null) {
            dto.setRequestedById(r.getRequestedBy().getId());
            dto.setRequestedByName(r.getRequestedBy().getName());
        }

        dto.setReason(r.getReason());
        dto.setStatus(r.getStatus());
        dto.setReviewComments(r.getReviewComments());

        if (r.getReviewedBy() != null) {
            dto.setReviewedByName(r.getReviewedBy().getName());
        }

        dto.setCreatedAt(r.getCreatedAt());
        return dto;
    }
}
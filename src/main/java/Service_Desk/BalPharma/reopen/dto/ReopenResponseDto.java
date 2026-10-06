package Service_Desk.BalPharma.reopen.dto;

import Service_Desk.BalPharma.reopen.entity.TicketReopenEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReopenResponseDto {

    private Long id;
    private String status;
    private Long ticketId;
    private String ticketCode;
    private String ticketSubject;
    private String ticketStatus;
    private String ticketPriority;
    private String ticketCategory;
    private String ticketDepartment;
    private Long requestedById;
    private String requestedByName;
    private String requestedByEmployeeId;
    private String reason;
    private Long reviewedById;
    private String reviewedByName;
    private String reviewComments;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ReopenResponseDto from(TicketReopenEntity r) {
        return new ReopenResponseDto(
                r.getId(),
                r.getStatus(),

                r.getTicket() != null ? r.getTicket().getId() : null,
                r.getTicket() != null ? r.getTicket().getTicketCode() : null,
                r.getTicket() != null ? r.getTicket().getSubject() : null,
                r.getTicket() != null ? r.getTicket().getStatus() : null,
                r.getTicket() != null ? r.getTicket().getPriority() : null,
                r.getTicket() != null && r.getTicket().getCategory() != null
                        ? r.getTicket().getCategory().getName() : null,
                r.getTicket() != null && r.getTicket().getDepartment() != null
                        ? r.getTicket().getDepartment().getName() : null,

                r.getRequestedBy() != null ? r.getRequestedBy().getId() : null,
                r.getRequestedBy() != null ? r.getRequestedBy().getName() : null,
                r.getRequestedBy() != null ? r.getRequestedBy().getEmployeeId() : null,

                r.getReason(),

                r.getReviewedBy() != null ? r.getReviewedBy().getId() : null,
                r.getReviewedBy() != null ? r.getReviewedBy().getName() : null,
                r.getReviewComments(),
                r.getReviewedAt(),

                r.getCreatedAt(),
                r.getUpdatedAt()
        );
    }
}
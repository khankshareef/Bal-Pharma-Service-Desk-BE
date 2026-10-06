package Service_Desk.BalPharma.ticket_request.dto;

import lombok.Data;

@Data
public class CreateTicketInfoRequestDto {
    private Long ticketId;
    private String message;
    private String attachments;
    private String attachmentNames;
}
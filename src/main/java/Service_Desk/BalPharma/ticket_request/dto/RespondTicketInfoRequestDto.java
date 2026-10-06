package Service_Desk.BalPharma.ticket_request.dto;

import lombok.Data;

@Data
public class RespondTicketInfoRequestDto {
    private String response;
    private String attachments;
    private String attachmentNames;
}
package Service_Desk.BalPharma.ticket.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTicketStatusDto {
    private String status;
    private String slaStatus;
    private String resolutionNotes;
    private String resolutionType;
}
package Service_Desk.BalPharma.reopen.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReopenDto {
    private Long ticketId;
    private String reason;
}
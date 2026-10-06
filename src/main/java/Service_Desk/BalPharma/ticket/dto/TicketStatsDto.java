package Service_Desk.BalPharma.ticket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketStatsDto {
    private long total;
    private long open;
    private long inProgress;
    private long resolved;
    private long closed;
    private long onTrack;
    private long breached;
    private long atRisk;
}
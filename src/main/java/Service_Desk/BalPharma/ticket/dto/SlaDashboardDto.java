package Service_Desk.BalPharma.ticket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlaDashboardDto {
    private long onTrack;
    private long atRisk;
    private long breached;
    private long total;
    private double overallCompliance;
}
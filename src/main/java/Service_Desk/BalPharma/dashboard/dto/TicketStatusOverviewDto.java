package Service_Desk.BalPharma.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketStatusOverviewDto {

    private long total;
    private long open;
    private long inProgress;
    private long resolved;
    private long closed;
    private long overdue;
    private long slaOnTrack;
    private long slaAtRisk;
    private int responseRatePercent;
}
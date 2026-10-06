package Service_Desk.BalPharma.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponseDto {

    private KpiCards kpis;
    private TicketStatusOverview statusOverview;
    private List<RecentActivityItem> recentActivity;
    private List<RecentTicketItem> recentTickets;

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    public static class KpiCards {
        private long totalTickets;
        private long openTickets;
        private long resolvedTickets;
        private long inProgressTickets;

        private String totalSlaText;
        private String openSlaText;
        private String resolvedAvgText;
        private String inProgressOverdueText;
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    public static class TicketStatusOverview {
        private long total;
        private long inProgress;
        private long open;
        private long resolved;
        private long closed;
        private long overdue;
        private long slaOnTrack;
        private int responseRatePercent;
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    public static class RecentActivityItem {
        private Long id;
        private String ticketId;
        private String subject;
        private String status;
        private String date;
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    public static class RecentTicketItem {
        private Long id;
        private String ticketId;
        private String subject;
        private String department;
        private String category;
        private String priority;
        private String status;
        private String sla;
        private String date;
    }
}
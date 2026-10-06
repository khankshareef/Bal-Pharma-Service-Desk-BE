package Service_Desk.BalPharma.analytics.dto;

import lombok.Data;

import java.util.List;

@Data
public class AnalyticsDto {

    private long totalTickets;
    private double avgResolutionHours;
    private int slaCompliancePercent;
    private double userSatisfaction;

    private int ticketsDeltaPercent;
    private double resolutionDeltaHours;
    private int slaDeltaPercent;
    private double satisfactionDelta;

    private List<Bucket> byPriority;

    private List<Bucket> byStatus;
    private long totalForStatus;

    private List<TrendPoint> monthlyTrend;

    private List<Bucket> byCategory;

    private List<HeatCell> heatmap;

    private List<UnitMetric> byUnit;

    @Data
    public static class Bucket {
        private String label;
        private long value;
        private String color;
    }

    @Data
    public static class TrendPoint {
        private String month;
        private long value;
    }

    @Data
    public static class HeatCell {
        private String date;
        private long count;
    }

    @Data
    public static class UnitMetric {
        private String unitName;
        private double hours;
    }
}
package Service_Desk.BalPharma.reports.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReportsDto {

    private long totalTickets;
    private long openTickets;
    private long resolvedTickets;
    private double avgResolutionHours;

    private List<Row> rows;

    @Data
    public static class Row {
        private String department;
        private String unit;
        private long open;
        private long closed;
        private double avgResolutionHours;
    }
}
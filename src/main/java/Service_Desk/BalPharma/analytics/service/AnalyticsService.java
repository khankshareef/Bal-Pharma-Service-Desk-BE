package Service_Desk.BalPharma.analytics.service;

import Service_Desk.BalPharma.analytics.dto.AnalyticsDto;
import Service_Desk.BalPharma.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private final TicketRepository ticketRepository;

    private static final Map<String, String> PRIORITY_COLORS = Map.of(
            "CRITICAL", "#9e1c1c",
            "HIGH",     "#c25c0e",
            "MEDIUM",   "#1f4e79",
            "LOW",      "#1e7e40"
    );

    private static final Map<String, String> STATUS_COLORS = Map.of(
            "OPEN",        "#c25c0e",
            "IN_PROGRESS", "#1f4e79",
            "RESOLVED",    "#1e7e40",
            "CLOSED",      "#6b21a8"
    );

    private static final Map<String, String> CATEGORY_COLORS = Map.of(
            "Hardware",     "#1f4e79",
            "Software",     "#1e7e40",
            "Network",      "#c25c0e",
            "Application",  "#6b21a8",
            "Access",       "#20948b"
    );

    public AnalyticsDto build(LocalDate from, LocalDate to) {

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end   = to.plusDays(1).atStartOfDay();   // exclusive

        var all = ticketRepository.findAllWithRelations();

        var inRange = all.stream()
                .filter(t -> t.getCreatedAt() != null)
                .filter(t -> !t.getCreatedAt().isBefore(start)
                        && t.getCreatedAt().isBefore(end))
                .toList();

        AnalyticsDto dto = new AnalyticsDto();

        dto.setTotalTickets(inRange.size());

        double avgHours = inRange.stream()
                .filter(t -> "RESOLVED".equals(t.getStatus())
                        || "CLOSED".equals(t.getStatus()))
                .filter(t -> t.getResolvedAt() != null)
                .mapToDouble(t -> java.time.Duration.between(
                        t.getCreatedAt(), t.getResolvedAt()).toMinutes() / 60.0)
                .average().orElse(0);
        dto.setAvgResolutionHours(Math.round(avgHours * 10.0) / 10.0);

        long total = inRange.size();
        long onTrack = inRange.stream()
                .filter(t -> "ON_TRACK".equals(t.getSlaStatus())).count();
        dto.setSlaCompliancePercent(total == 0 ? 0
                : (int) Math.round(onTrack * 100.0 / total));

        dto.setUserSatisfaction(4.2);

        long days = java.time.temporal.ChronoUnit.DAYS.between(from, to) + 1;
        LocalDateTime prevStart = start.minusDays(days);
        LocalDateTime prevEnd   = start;
        long prevTotal = all.stream()
                .filter(t -> t.getCreatedAt() != null)
                .filter(t -> !t.getCreatedAt().isBefore(prevStart)
                        && t.getCreatedAt().isBefore(prevEnd))
                .count();
        dto.setTicketsDeltaPercent(prevTotal == 0 ? 0
                : (int) Math.round((total - prevTotal) * 100.0 / prevTotal));
        dto.setSlaDeltaPercent(0);
        dto.setResolutionDeltaHours(0);
        dto.setSatisfactionDelta(0);

        Map<String, Long> pCounts = inRange.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getPriority() == null ? "LOW" : t.getPriority(),
                        Collectors.counting()));
        dto.setByPriority(buildBuckets(pCounts, PRIORITY_COLORS,
                List.of("CRITICAL", "HIGH", "MEDIUM", "LOW")));

        Map<String, Long> sCounts = inRange.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getStatus() == null ? "OPEN" : t.getStatus(),
                        Collectors.counting()));
        dto.setByStatus(buildBuckets(sCounts, STATUS_COLORS,
                List.of("OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED")));
        dto.setTotalForStatus(inRange.size());

        DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("MMM");
        Map<String, Long> monthCounts = new TreeMap<>();
        for (int i = 7; i >= 0; i--) {
            LocalDate m = LocalDate.now().minusMonths(i).withDayOfMonth(1);
            monthCounts.put(m.format(monthFmt), 0L);
        }
        all.forEach(t -> {
            if (t.getCreatedAt() == null) return;
            String key = t.getCreatedAt().format(monthFmt);
            if (monthCounts.containsKey(key)) {
                monthCounts.merge(key, 1L, Long::sum);
            }
        });
        dto.setMonthlyTrend(monthCounts.entrySet().stream().map(e -> {
            AnalyticsDto.TrendPoint p = new AnalyticsDto.TrendPoint();
            p.setMonth(e.getKey());
            p.setValue(e.getValue());
            return p;
        }).toList());

        Map<String, Long> cCounts = inRange.stream()
                .filter(t -> t.getCategory() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.counting()));
        dto.setByCategory(buildBuckets(cCounts, CATEGORY_COLORS, null));

        // ---------- Heatmap (last N days) ----------
        List<AnalyticsDto.HeatCell> cells = new ArrayList<>();
        DateTimeFormatter iso = DateTimeFormatter.ISO_DATE;
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            LocalDateTime dayStart = d.atStartOfDay();
            LocalDateTime dayEnd   = d.plusDays(1).atStartOfDay();
            long c = inRange.stream()
                    .filter(t -> t.getCreatedAt() != null)
                    .filter(t -> !t.getCreatedAt().isBefore(dayStart)
                            && t.getCreatedAt().isBefore(dayEnd))
                    .count();
            AnalyticsDto.HeatCell cell = new AnalyticsDto.HeatCell();
            cell.setDate(d.format(iso));
            cell.setCount(c);
            cells.add(cell);
        }
        dto.setHeatmap(cells);

        // ---------- Avg resolution by unit ----------
        Map<String, List<Double>> unitHours = new HashMap<>();
        inRange.forEach(t -> {
            if (t.getResolvedAt() == null || t.getCreatedAt() == null) return;
            String unit = t.getUnitName() != null ? t.getUnitName() : "Unknown";
            double h = java.time.Duration.between(
                    t.getCreatedAt(), t.getResolvedAt()).toMinutes() / 60.0;
            unitHours.computeIfAbsent(unit, k -> new ArrayList<>()).add(h);
        });
        List<AnalyticsDto.UnitMetric> units = unitHours.entrySet().stream()
                .map(e -> {
                    AnalyticsDto.UnitMetric m = new AnalyticsDto.UnitMetric();
                    m.setUnitName(e.getKey());
                    m.setHours(Math.round(
                            e.getValue().stream().mapToDouble(d -> d).average().orElse(0)
                                    * 10.0) / 10.0);
                    return m;
                })
                .sorted(Comparator.comparing(AnalyticsDto.UnitMetric::getUnitName))
                .toList();
        dto.setByUnit(units);

        return dto;
    }

    private List<AnalyticsDto.Bucket> buildBuckets(Map<String, Long> counts,
                                                   Map<String, String> colors,
                                                   List<String> order) {
        List<String> keys = order != null ? order : new ArrayList<>(counts.keySet());
        return keys.stream().map(k -> {
            AnalyticsDto.Bucket b = new AnalyticsDto.Bucket();
            b.setLabel(prettify(k));
            b.setValue(counts.getOrDefault(k, 0L));
            b.setColor(colors.getOrDefault(k, "#1f4e79"));
            return b;
        }).toList();
    }

    private String prettify(String s) {
        if (s == null || s.isEmpty()) return s;
        String[] parts = s.split("_");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p.isEmpty()) continue;
            sb.append(Character.toUpperCase(p.charAt(0)))
                    .append(p.substring(1).toLowerCase())
                    .append(' ');
        }
        return sb.toString().trim();
    }
}
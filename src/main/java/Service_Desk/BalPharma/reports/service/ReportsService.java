package Service_Desk.BalPharma.reports.service;

import Service_Desk.BalPharma.reports.dto.ReportsDto;
import Service_Desk.BalPharma.ticket.entity.TicketEntity;
import Service_Desk.BalPharma.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportsService {

    private final TicketRepository ticketRepository;

    public ReportsDto build() {

        List<TicketEntity> all = ticketRepository.findAllWithRelations();

        ReportsDto dto = new ReportsDto();

        // ----- KPI row -----
        dto.setTotalTickets(all.size());
        dto.setOpenTickets(all.stream()
                .filter(t -> "OPEN".equalsIgnoreCase(t.getStatus())
                        || "IN_PROGRESS".equalsIgnoreCase(t.getStatus()))
                .count());
        dto.setResolvedTickets(all.stream()
                .filter(t -> "RESOLVED".equalsIgnoreCase(t.getStatus())
                        || "CLOSED".equalsIgnoreCase(t.getStatus()))
                .count());

        dto.setAvgResolutionHours(avgHours(
                all.stream()
                        .filter(t -> t.getResolvedAt() != null
                                && t.getCreatedAt() != null)
                        .toList()));

        Map<String, List<TicketEntity>> grouped = new LinkedHashMap<>();

        for (TicketEntity t : all) {
            String dep = t.getDepartment() != null ? t.getDepartment().getName() : "—";
            String unit = t.getUnitName() != null ? t.getUnitName() : "All";
            String key = dep + "||" + unit;
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(t);
        }

        List<ReportsDto.Row> rows = grouped.entrySet().stream()
                .map(e -> {
                    ReportsDto.Row r = new ReportsDto.Row();
                    String[] parts = e.getKey().split("\\|\\|", 2);
                    r.setDepartment(parts[0]);
                    r.setUnit(parts[1]);

                    List<TicketEntity> list = e.getValue();

                    r.setOpen(list.stream()
                            .filter(t -> "OPEN".equalsIgnoreCase(t.getStatus())
                                    || "IN_PROGRESS".equalsIgnoreCase(t.getStatus()))
                            .count());

                    r.setClosed(list.stream()
                            .filter(t -> "CLOSED".equalsIgnoreCase(t.getStatus())
                                    || "RESOLVED".equalsIgnoreCase(t.getStatus()))
                            .count());

                    r.setAvgResolutionHours(avgHours(
                            list.stream()
                                    .filter(t -> t.getResolvedAt() != null
                                            && t.getCreatedAt() != null)
                                    .toList()));

                    return r;
                })
                .sorted(Comparator
                        .comparing(ReportsDto.Row::getDepartment)
                        .thenComparing(ReportsDto.Row::getUnit))
                .toList();

        dto.setRows(rows);

        return dto;
    }

    private double avgHours(List<TicketEntity> list) {
        if (list.isEmpty()) return 0;
        return Math.round(
                list.stream()
                        .mapToDouble(t -> Duration.between(
                                t.getCreatedAt(), t.getResolvedAt()).toMinutes() / 60.0)
                        .average().orElse(0)
                        * 10.0) / 10.0;
    }
}
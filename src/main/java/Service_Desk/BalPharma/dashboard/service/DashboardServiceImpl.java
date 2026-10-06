package Service_Desk.BalPharma.dashboard.service;

import Service_Desk.BalPharma.auth.entity.AuthEntity;
import Service_Desk.BalPharma.auth.repository.AuthRepository;
import Service_Desk.BalPharma.dashboard.dto.DashboardResponseDto;
import Service_Desk.BalPharma.dashboard.dto.DashboardResponseDto.*;
import Service_Desk.BalPharma.dashboard.dto.TicketStatusOverviewDto;
import Service_Desk.BalPharma.exception.AuthException;
import Service_Desk.BalPharma.ticket.entity.TicketEntity;
import Service_Desk.BalPharma.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final TicketRepository ticketRepository;
    private final AuthRepository authRepository;


    @Override
    public DashboardResponseDto getDashboard() {

        long total      = ticketRepository.count();
        long open       = ticketRepository.countByStatus("OPEN");
        long inProgress = ticketRepository.countByStatus("IN_PROGRESS");
        long resolved   = ticketRepository.countByStatus("RESOLVED");
        long closed     = ticketRepository.countByStatus("CLOSED");
        long onTrack    = ticketRepository.countBySlaStatus("ON_TRACK");
        long breached   = ticketRepository.countBySlaStatus("BREACHED");

        return buildResponse(
                total, open, inProgress, resolved, closed, onTrack, breached,
                ticketRepository.findAllWithRelations()
        );
    }


    @Override
    public DashboardResponseDto getEmployeeDashboard(String employeeId) {

        AuthEntity user = authRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new AuthException("Employee not found: " + employeeId));

        List<TicketEntity> myTickets = ticketRepository.findByCreatedById(user.getId());

        long total      = myTickets.size();
        long open       = countBy(myTickets, t -> "OPEN".equals(t.getStatus()));
        long inProgress = countBy(myTickets, t -> "IN_PROGRESS".equals(t.getStatus()));
        long resolved   = countBy(myTickets, t -> "RESOLVED".equals(t.getStatus()));
        long closed     = countBy(myTickets, t -> "CLOSED".equals(t.getStatus()));
        long onTrack    = countBy(myTickets, t -> "ON_TRACK".equals(t.getSlaStatus()));
        long breached   = countBy(myTickets, t -> "BREACHED".equals(t.getSlaStatus()));

        return buildResponse(total, open, inProgress, resolved, closed, onTrack, breached, myTickets);
    }


    @Override
    public DashboardResponseDto getExecutiveDashboard(String employeeId) {

        AuthEntity exec = authRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new AuthException("Executive not found: " + employeeId));

        List<TicketEntity> assigned = ticketRepository.findByAssignedToId(exec.getId());

        long total      = assigned.size();
        long open       = countBy(assigned, t -> "OPEN".equals(t.getStatus()));
        long inProgress = countBy(assigned, t -> "IN_PROGRESS".equals(t.getStatus()));
        long resolved   = countBy(assigned, t -> "RESOLVED".equals(t.getStatus()));
        long closed     = countBy(assigned, t -> "CLOSED".equals(t.getStatus()));
        long onTrack    = countBy(assigned, t -> "ON_TRACK".equals(t.getSlaStatus()));
        long breached   = countBy(assigned, t -> "BREACHED".equals(t.getSlaStatus()));

        return buildResponse(total, open, inProgress, resolved, closed, onTrack, breached, assigned);
    }


    @Override
    public TicketStatusOverviewDto getStatusOverview() {
        long total      = ticketRepository.count();
        long open       = ticketRepository.countByStatus("OPEN");
        long inProgress = ticketRepository.countByStatus("IN_PROGRESS");
        long resolved   = ticketRepository.countByStatus("RESOLVED");
        long closed     = ticketRepository.countByStatus("CLOSED");
        long onTrack    = ticketRepository.countBySlaStatus("ON_TRACK");
        long breached   = ticketRepository.countBySlaStatus("BREACHED");
        long atRisk     = ticketRepository.countBySlaStatus("AT_RISK");

        return new TicketStatusOverviewDto(
                total, open, inProgress, resolved, closed,
                breached, onTrack, atRisk,
                pct(onTrack, total)
        );
    }

    @Override
    public TicketStatusOverviewDto getEmployeeStatusOverview(String employeeId) {
        AuthEntity user = authRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new AuthException("Employee not found: " + employeeId));

        List<TicketEntity> myTickets = ticketRepository.findByCreatedById(user.getId());
        return overviewFrom(myTickets);
    }

    @Override
    public TicketStatusOverviewDto getExecutiveStatusOverview(String employeeId) {
        AuthEntity exec = authRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new AuthException("Executive not found: " + employeeId));

        List<TicketEntity> assigned = ticketRepository.findByAssignedToId(exec.getId());
        return overviewFrom(assigned);
    }


    private DashboardResponseDto buildResponse(
            long total, long open, long inProgress, long resolved,
            long closed, long onTrack, long breached,
            List<TicketEntity> sourceTickets) {

        KpiCards k = new KpiCards();
        k.setTotalTickets(total);
        k.setOpenTickets(open);
        k.setResolvedTickets(resolved);
        k.setInProgressTickets(inProgress);
        k.setTotalSlaText("SLA: " + pct(onTrack, total) + "% on track");
        k.setOpenSlaText("SLA: " + pct(onTrack, total) + "% on track");
        k.setResolvedAvgText("Avg: 8h");
        k.setInProgressOverdueText("Overdue: " + breached);

        TicketStatusOverview s = new TicketStatusOverview();
        s.setTotal(total);
        s.setOpen(open);
        s.setInProgress(inProgress);
        s.setResolved(resolved);
        s.setClosed(closed);
        s.setOverdue(breached);
        s.setSlaOnTrack(onTrack);
        s.setResponseRatePercent(pct(onTrack, total));

        List<RecentActivityItem> activity = sourceTickets.stream()
                .limit(6).map(this::toActivity).toList();

        List<RecentTicketItem> recent = sourceTickets.stream()
                .limit(5).map(this::toRecentTicket).toList();

        DashboardResponseDto dto = new DashboardResponseDto();
        dto.setKpis(k);
        dto.setStatusOverview(s);
        dto.setRecentActivity(activity);
        dto.setRecentTickets(recent);
        return dto;
    }

    private TicketStatusOverviewDto overviewFrom(List<TicketEntity> tickets) {
        long total      = tickets.size();
        long open       = countBy(tickets, t -> "OPEN".equals(t.getStatus()));
        long inProgress = countBy(tickets, t -> "IN_PROGRESS".equals(t.getStatus()));
        long resolved   = countBy(tickets, t -> "RESOLVED".equals(t.getStatus()));
        long closed     = countBy(tickets, t -> "CLOSED".equals(t.getStatus()));
        long onTrack    = countBy(tickets, t -> "ON_TRACK".equals(t.getSlaStatus()));
        long breached   = countBy(tickets, t -> "BREACHED".equals(t.getSlaStatus()));
        long atRisk     = countBy(tickets, t -> "AT_RISK".equals(t.getSlaStatus()));

        return new TicketStatusOverviewDto(
                total, open, inProgress, resolved, closed,
                breached, onTrack, atRisk,
                pct(onTrack, total)
        );
    }

    private long countBy(List<TicketEntity> list,
                         java.util.function.Predicate<TicketEntity> test) {
        return list.stream().filter(test).count();
    }

    private int pct(long part, long whole) {
        return whole == 0 ? 0 : (int) Math.round(part * 100.0 / whole);
    }

    private RecentActivityItem toActivity(TicketEntity t) {
        return new RecentActivityItem(
                t.getId(),
                t.getTicketCode(),
                t.getSubject(),
                humanizeStatus(t.getStatus()),
                t.getCreatedAt() != null ? t.getCreatedAt().format(DATE_FMT) : ""
        );
    }

    private RecentTicketItem toRecentTicket(TicketEntity t) {
        return new RecentTicketItem(
                t.getId(),
                t.getTicketCode(),
                t.getSubject(),
                t.getDepartment() != null ? t.getDepartment().getName() : "",
                t.getCategory() != null ? t.getCategory().getName() : "",
                humanizePriority(t.getPriority()),
                humanizeStatus(t.getStatus()),
                humanizeSla(t.getSlaStatus()),
                t.getCreatedAt() != null ? t.getCreatedAt().format(DATE_FMT) : ""
        );
    }

    private String humanizeStatus(String s) {
        if (s == null) return "";
        return switch (s) {
            case "OPEN" -> "Open";
            case "IN_PROGRESS" -> "In Progress";
            case "RESOLVED" -> "Resolved";
            case "CLOSED" -> "Closed";
            default -> s;
        };
    }

    private String humanizePriority(String p) {
        if (p == null || p.isBlank()) return "";
        return p.charAt(0) + p.substring(1).toLowerCase();
    }

    private String humanizeSla(String s) {
        if (s == null) return "";
        return switch (s) {
            case "ON_TRACK" -> "On Track";
            case "AT_RISK" -> "At Risk";
            case "BREACHED" -> "Breached";
            default -> s;
        };
    }
}
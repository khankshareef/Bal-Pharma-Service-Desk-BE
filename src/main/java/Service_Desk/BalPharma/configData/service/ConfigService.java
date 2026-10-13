package Service_Desk.BalPharma.configData.service;

import Service_Desk.BalPharma.configData.dto.PriorityConfigDto;
import Service_Desk.BalPharma.configData.dto.StatusConfigDto;
import Service_Desk.BalPharma.configData.repository.PriorityRepository;
import Service_Desk.BalPharma.configData.repository.StatusRepository;
import Service_Desk.BalPharma.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConfigService {

    private final PriorityRepository priorityRepository;
    private final StatusRepository statusRepository;
    private final TicketRepository ticketRepository;

    private static String normalize(String value) {
        if (value == null) return "";
        return value.trim()
                .toUpperCase()
                .replaceAll("[\\s\\-]+", "_");
    }

    public List<PriorityConfigDto> getPriorities() {

        // count tickets per normalized priority string ("HIGH", "CRITICAL", ...)
        Map<String, Long> counts = ticketRepository.findAllWithRelations().stream()
                .filter(t -> t.getPriority() != null && !t.getPriority().isBlank())
                .collect(Collectors.groupingBy(
                        t -> normalize(t.getPriority()),
                        Collectors.counting()
                ));

        System.out.println(">>> [ConfigService] priority counts: " + counts);

        List<PriorityConfigDto> out = priorityRepository
                .findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(p -> {
                    PriorityConfigDto d = new PriorityConfigDto();
                    d.setId(p.getId());
                    d.setPriorityId(p.getPriorityCode());
                    d.setName(p.getName());
                    d.setActive(p.getActive());

                    // try priorityCode first (the value tickets store), then name
                    long tickets = counts.getOrDefault(
                            normalize(p.getPriorityCode()), 0L);
                    if (tickets == 0L) {
                        tickets = counts.getOrDefault(
                                normalize(p.getName()), 0L);
                    }

                    d.setTickets(tickets);
                    return d;
                })
                .toList();

        System.out.println(">>> [ConfigService] priorities returned: "
                + out.size());
        return out;
    }

    public List<StatusConfigDto> getStatuses() {

        // count tickets per normalized status ("OPEN", "IN_PROGRESS", ...)
        Map<String, Long> counts = ticketRepository.findAllWithRelations().stream()
                .filter(t -> t.getStatus() != null && !t.getStatus().isBlank())
                .collect(Collectors.groupingBy(
                        t -> normalize(t.getStatus()),
                        Collectors.counting()
                ));

        System.out.println(">>> [ConfigService] status counts: " + counts);

        List<StatusConfigDto> out = statusRepository
                .findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(s -> {
                    StatusConfigDto d = new StatusConfigDto();
                    d.setId(s.getId());
                    d.setStatusId(s.getStatusCode());
                    d.setName(s.getName());
                    d.setActive(s.getActive());

                    long tickets = counts.getOrDefault(
                            normalize(s.getStatusCode()), 0L);
                    if (tickets == 0L) {
                        tickets = counts.getOrDefault(
                                normalize(s.getName()), 0L);
                    }

                    d.setTickets(tickets);
                    return d;
                })
                .toList();

        System.out.println(">>> [ConfigService] statuses returned: "
                + out.size());
        return out;
    }
}
package Service_Desk.BalPharma.configData.service;

import Service_Desk.BalPharma.configData.dto.PriorityConfigDto;
import Service_Desk.BalPharma.configData.dto.StatusConfigDto;
import Service_Desk.BalPharma.configData.repository.PriorityRepository;
import Service_Desk.BalPharma.configData.repository.StatusRepository;
import Service_Desk.BalPharma.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConfigService {

    private final PriorityRepository priorityRepository;
    private final StatusRepository   statusRepository;
    private final TicketRepository   ticketRepository;


    public List<PriorityConfigDto> getPriorities() {

        Map<String, Long> counts = new HashMap<>();
        for (Object[] row : ticketRepository.countGroupedByPriority()) {
            counts.put((String) row[0], (Long) row[1]);
        }

        return priorityRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(p -> {
                    PriorityConfigDto d = new PriorityConfigDto();
                    d.setId(p.getId());
                    d.setPriorityId(p.getPriorityCode());
                    d.setName(p.getName());
                    d.setActive(p.getActive());

                    long ticketCount = resolvePriorityCount(p, counts);
                    d.setTickets(ticketCount);
                    return d;
                })
                .toList();
    }

    private long resolvePriorityCount(
            Service_Desk.BalPharma.configData.entity.PriorityEntity p,
            Map<String, Long> counts) {

        if (p.getName() == null) return 0L;

        String upper = p.getName().toUpperCase().replace(' ', '_');

        if (counts.containsKey(upper)) return counts.get(upper);

        switch (upper) {
            case "MEDIUM":  return counts.getOrDefault("NORMAL",  0L)
                    + counts.getOrDefault("MEDIUM",  0L);
            case "DEFERRED":return counts.getOrDefault("DEFERRED",0L)
                    + counts.getOrDefault("LOW",     0L);
            default:        return 0L;
        }
    }


    public List<StatusConfigDto> getStatuses() {

        Map<String, Long> counts = new HashMap<>();
        for (Object[] row : ticketRepository.countGroupedByStatus()) {
            counts.put((String) row[0], (Long) row[1]);
        }

        return statusRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(s -> {
                    StatusConfigDto d = new StatusConfigDto();
                    d.setId(s.getId());
                    d.setStatusId(s.getStatusCode());
                    d.setName(s.getName());
                    d.setActive(s.getActive());
                    d.setTickets(resolveStatusCount(s, counts));
                    return d;
                })
                .toList();
    }

    private long resolveStatusCount(
            Service_Desk.BalPharma.configData.entity.StatusEntity s,
            Map<String, Long> counts) {

        if (s.getName() == null) return 0L;

        String upper = s.getName().toUpperCase().replace(' ', '_');

        if (counts.containsKey(upper)) return counts.get(upper);

        switch (upper) {
            case "RESOLVED": return counts.getOrDefault("RESOLVED", 0L);
            case "CANCELLED":
            case "CANCELED": return counts.getOrDefault("CANCELLED", 0L)
                    + counts.getOrDefault("CANCELED", 0L);
            default:         return 0L;
        }
    }
}
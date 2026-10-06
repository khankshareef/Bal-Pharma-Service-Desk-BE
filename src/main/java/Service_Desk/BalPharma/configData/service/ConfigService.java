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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConfigService {

    private final PriorityRepository priorityRepository;
    private final StatusRepository statusRepository;
    private final TicketRepository ticketRepository;

    public List<PriorityConfigDto> getPriorities() {

        // count tickets per priority string ("HIGH", "CRITICAL", ...)
        var counts = ticketRepository.findAllWithRelations().stream()
                .filter(t -> t.getPriority() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getPriority().toUpperCase(),
                        Collectors.counting()));

        return priorityRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(p -> {
                    PriorityConfigDto d = new PriorityConfigDto();
                    d.setId(p.getId());
                    d.setPriorityId(p.getPriorityCode());
                    d.setName(p.getName());
                    d.setActive(p.getActive());
                    d.setTickets(counts.getOrDefault(
                            p.getName().toUpperCase(), 0L));
                    return d;
                })
                .toList();
    }

    public List<StatusConfigDto> getStatuses() {

        var counts = ticketRepository.findAllWithRelations().stream()
                .filter(t -> t.getStatus() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getStatus().toUpperCase(),
                        Collectors.counting()));

        return statusRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(s -> {
                    StatusConfigDto d = new StatusConfigDto();
                    d.setId(s.getId());
                    d.setStatusId(s.getStatusCode());
                    d.setName(s.getName());
                    d.setActive(s.getActive());
                    d.setTickets(counts.getOrDefault(
                            s.getName().toUpperCase().replace(" ", "_"), 0L));
                    return d;
                })
                .toList();
    }
}
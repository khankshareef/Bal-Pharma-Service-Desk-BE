package Service_Desk.BalPharma.dashboard.service;

import Service_Desk.BalPharma.auth.entity.AuthEntity;
import Service_Desk.BalPharma.auth.repository.AuthRepository;
import Service_Desk.BalPharma.dashboard.dto.SuperManagerDashboardDto;
import Service_Desk.BalPharma.location.AccountStatus;
import Service_Desk.BalPharma.location.Role;
import Service_Desk.BalPharma.notification.entity.NotificationEntity;
import Service_Desk.BalPharma.notification.repository.NotificationRepository;
import Service_Desk.BalPharma.unit.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SuperManagerDashboardService {

    private final AuthRepository authRepository;
    private final UnitRepository unitRepository;
    private final NotificationRepository notificationRepository;

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("hh:mm a");

    public SuperManagerDashboardDto load() {

        SuperManagerDashboardDto dto = new SuperManagerDashboardDto();

        List<AuthEntity> users = authRepository.findAll();

        long total     = users.size();
        long active    = users.stream()
                .filter(u -> u.getStatus() == AccountStatus.ACTIVE).count();
        long employees = users.stream()
                .filter(u -> u.hasRole(Role.EMPLOYEE)).count();
        long execs     = users.stream()
                .filter(u -> u.hasRole(Role.EXECUTIVE)).count();

        Set<String> distinctUnits = new HashSet<>();
        for (AuthEntity u : users) {
            if (u.getAllowedLocations() != null) {
                u.getAllowedLocations().forEach(loc -> distinctUnits.add(loc.getUnitCode()));
            }
        }

        dto.setTotalUsers(total);
        dto.setActiveUsers(active);
        dto.setEmployees(employees);
        dto.setExecutives(execs);
        dto.setLocations(distinctUnits.size());

        dto.setTotalUsersHint("+3 this month");
        dto.setActiveUsersHint(total == 0 ? "0% active"
                : Math.round(active * 100.0 / total) + "% active");
        dto.setEmployeesHint(total == 0 ? "0% of users"
                : Math.round(employees * 100.0 / total) + "% of users");
        dto.setExecutivesHint(total == 0 ? "0% of users"
                : Math.round(execs * 100.0 / total) + "% of users");
        dto.setLocationsHint("Across " + distinctUnits.size() + " locations");

        Map<String, Long> countsByUnit = new HashMap<>();
        Map<String, SuperManagerDashboardDto.UnitCount> detail = new HashMap<>();

        for (AuthEntity u : users) {
            if (u.getAllowedLocations() == null) continue;
            u.getAllowedLocations().forEach(loc -> {
                countsByUnit.merge(loc.getUnitCode(), 1L, Long::sum);
                detail.computeIfAbsent(loc.getUnitCode(), k -> {
                    SuperManagerDashboardDto.UnitCount c =
                            new SuperManagerDashboardDto.UnitCount();
                    c.setUnitCode(loc.getUnitCode());
                    c.setUnitName(loc.getUnitName());
                    c.setAddress(loc.getAddress());
                    return c;
                });
            });
        }

        List<SuperManagerDashboardDto.UnitCount> units = countsByUnit.entrySet().stream()
                .map(e -> {
                    SuperManagerDashboardDto.UnitCount c = detail.get(e.getKey());
                    c.setUserCount(e.getValue());
                    return c;
                })
                .sorted(Comparator.comparing(
                        SuperManagerDashboardDto.UnitCount::getUnitName))
                .collect(Collectors.toList());
        dto.setUnits(units);
        List<SuperManagerDashboardDto.ActivityItem> recent = new ArrayList<>();
        try {
            var rows = notificationRepository.findAll();   // or a paged call
            rows.stream()
                    .sorted(Comparator.comparing(NotificationEntity::getCreatedAt).reversed())
                    .limit(4)
                    .forEach(n -> {
                        SuperManagerDashboardDto.ActivityItem a =
                                new SuperManagerDashboardDto.ActivityItem();
                        a.setId(n.getId());
                        a.setTime("Today " + n.getCreatedAt().format(TIME_FMT));
                        a.setActivity(n.getTitle() + " — " + n.getMessage());
                        recent.add(a);
                    });
        } catch (Exception ignored) {
        }
        dto.setRecentActivity(recent);

        return dto;
    }
}
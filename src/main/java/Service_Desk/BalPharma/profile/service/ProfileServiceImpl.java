package Service_Desk.BalPharma.profile.service;

import Service_Desk.BalPharma.auth.entity.AuthEntity;
import Service_Desk.BalPharma.auth.entity.UnitAssignment;
import Service_Desk.BalPharma.auth.entity.UserRoleUnit;
import Service_Desk.BalPharma.auth.repository.AuthRepository;
import Service_Desk.BalPharma.auth.repository.UserRoleUnitRepository;
import Service_Desk.BalPharma.exception.AuthException;
import Service_Desk.BalPharma.location.Role;
import Service_Desk.BalPharma.profile.dto.ProfileResponseDto;
import Service_Desk.BalPharma.super_manager.dto.UnitAssignmentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileServiceImpl implements ProfileService {

    private final AuthRepository authRepository;
    private final UserRoleUnitRepository userRoleUnitRepository;   // ← added

    @Override
    public ProfileResponseDto getByEmployeeId(String employeeId, Role activeRole) {
        AuthEntity user = authRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new AuthException("Employee ID not found: " + employeeId));
        return toProfile(user, activeRole);
    }

    @Override
    public ProfileResponseDto getById(Long id, Role activeRole) {
        AuthEntity user = authRepository.findById(id)
                .orElseThrow(() -> new AuthException("User not found with id: " + id));
        return toProfile(user, activeRole);
    }

    @Override
    public ProfileResponseDto getByEmployeeId(String employeeId) {
        AuthEntity user = authRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new AuthException("Employee ID not found: " + employeeId));
        return toProfile(user, fallbackRole(user));
    }

    @Override
    public ProfileResponseDto getById(Long id) {
        AuthEntity user = authRepository.findById(id)
                .orElseThrow(() -> new AuthException("User not found with id: " + id));
        return toProfile(user, fallbackRole(user));
    }


    private ProfileResponseDto toProfile(AuthEntity e, Role activeRole) {

        ProfileResponseDto p = new ProfileResponseDto();

        p.setId(e.getId());
        p.setEmployeeId(e.getEmployeeId());
        p.setName(e.getName());

        List<String> roleNames = e.getRoles() == null
                ? List.of()
                : e.getRoles().stream().map(Enum::name).toList();
        p.setRoles(roleNames);

        p.setActiveRole(activeRole != null ? activeRole.name() : null);
        p.setRoleDisplay(humanizeRole(activeRole));
        p.setRole(activeRole != null ? activeRole.name() : null);

        p.setDepartment(e.getDepartment());
        p.setStatus(e.getStatus());
        p.setFirstTimeLogin(e.getFirstTimeLogin());
        p.setActive(e.getStatus() != null
                && e.getStatus().name().equals("ACTIVE"));

        p.setPrimaryLocation(e.getPrimaryLocation());

        Set<String> activeRoleCodes = resolveAllowedCodesForRole(e, activeRole);

        List<UnitAssignmentDto> units = e.getAllowedLocations() == null
                ? List.of()
                : e.getAllowedLocations().stream()
                .filter(u -> activeRoleCodes.contains(u.getUnitCode()))
                .map(u -> new UnitAssignmentDto(
                        u.getUnitCode(),
                        u.getUnitName(),
                        u.getAddress(),
                        u.getPortCode(),
                        u.getLatitude(),
                        u.getLongitude(),
                        u.getRadiusMeters() != null ? u.getRadiusMeters() : 100
                ))
                .toList();

        p.setAssignedUnits(units);
        p.setLocationCount(units.size());

        p.setLastLoginAt(e.getLastLoginAt());
        p.setLastLoginLocation(e.getLastLoginLocation());
        p.setCreatedAt(e.getCreatedAt());
        p.setPasswordLastReset(e.getPasswordLastReset());
        p.setResetByName(e.getResetBy() != null ? e.getResetBy().getName() : null);

        p.setInitials(computeInitials(e.getName()));

        LocalDateTime anchor = e.getCreatedAt() != null
                ? e.getCreatedAt()
                : e.getLastLoginAt();

        p.setAccountAgeDays(
                anchor != null
                        ? ChronoUnit.DAYS.between(anchor, LocalDateTime.now())
                        : null
        );

        return p;
    }


    private Role fallbackRole(AuthEntity user) {
        if (user.getRoles() == null || user.getRoles().isEmpty()) return null;

        Role[] hierarchy = {
                Role.SUPER_MANAGER, Role.ADMIN, Role.DEPUTY_MANAGER,
                Role.EXECUTIVE, Role.EMPLOYEE
        };
        for (Role r : hierarchy) {
            if (user.getRoles().contains(r)) return r;
        }
        return user.getRoles().iterator().next();
    }

    private Set<String> resolveAllowedCodesForRole(AuthEntity user, Role role) {

        if (role == Role.ADMIN || role == Role.SUPER_MANAGER) {
            // privileged: all units
            return user.getAllowedLocations() == null
                    ? Set.of()
                    : user.getAllowedLocations().stream()
                    .map(UnitAssignment::getUnitCode)
                    .collect(Collectors.toSet());
        }

        Set<String> roleScoped = userRoleUnitRepository
                .findByUserIdAndRole(user.getId(), role.name())
                .stream()
                .map(UserRoleUnit::getUnitCode)
                .collect(Collectors.toSet());

        if (!roleScoped.isEmpty()) return roleScoped;

        // fallback: all assigned
        Set<String> all = new HashSet<>();
        if (user.getPrimaryLocation() != null) all.add(user.getPrimaryLocation());
        if (user.getAllowedLocations() != null) {
            for (UnitAssignment u : user.getAllowedLocations()) {
                all.add(u.getUnitCode());
            }
        }
        return all;
    }

    private String humanizeRole(Role role) {
        if (role == null) return null;
        return switch (role) {
            case EMPLOYEE       -> "Employee";
            case EXECUTIVE      -> "Executive";
            case DEPUTY_MANAGER -> "Deputy Manager";
            case SUPER_MANAGER  -> "Super Manager";
            case ADMIN          -> "Admin";
        };
    }

    private String computeInitials(String name) {
        if (name == null || name.isBlank()) return null;
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(2, parts.length); i++) {
            if (!parts[i].isEmpty()) {
                sb.append(Character.toUpperCase(parts[i].charAt(0)));
            }
        }
        return sb.toString();
    }
}
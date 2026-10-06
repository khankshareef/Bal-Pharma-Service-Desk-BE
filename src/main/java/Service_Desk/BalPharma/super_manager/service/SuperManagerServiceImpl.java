package Service_Desk.BalPharma.super_manager.service;

import Service_Desk.BalPharma.auth.entity.AuthEntity;
import Service_Desk.BalPharma.auth.entity.UnitAssignment;
import Service_Desk.BalPharma.auth.entity.UserRoleUnit;
import Service_Desk.BalPharma.auth.repository.AuthRepository;
import Service_Desk.BalPharma.auth.repository.UserRoleUnitRepository;
import Service_Desk.BalPharma.exception.AuthException;
import Service_Desk.BalPharma.location.AccountStatus;
import Service_Desk.BalPharma.location.Role;
import Service_Desk.BalPharma.super_manager.dto.*;
import Service_Desk.BalPharma.unit.entity.UnitEntity;
import Service_Desk.BalPharma.unit.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SuperManagerServiceImpl implements SuperManagerService {

    private final AuthRepository authRepository;
    private final UnitRepository unitRepository;
    private final UserRoleUnitRepository userRoleUnitRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Role[] ROLE_HIERARCHY = {
            Role.SUPER_MANAGER, Role.ADMIN, Role.DEPUTY_MANAGER,
            Role.EXECUTIVE, Role.EMPLOYEE,
    };


    @Override
    public UserResponseDto createUser(CreateUserDto dto) {

        if (dto.getEmployeeId() == null || dto.getEmployeeId().isBlank())
            throw new AuthException("Employee ID is required");
        if (dto.getName() == null || dto.getName().isBlank())
            throw new AuthException("Name is required");
        if (dto.getPassword() == null || dto.getPassword().isBlank())
            throw new AuthException("Password is required");
        if (dto.getRoles() == null || dto.getRoles().isEmpty())
            throw new AuthException("At least one role is required");
        if (dto.getDepartment() == null || dto.getDepartment().isBlank())
            throw new AuthException("Department is required");
        if (dto.getAllowedLocations() == null || dto.getAllowedLocations().isEmpty())
            throw new AuthException("At least one Unit must be assigned");

        if (authRepository.findByEmployeeId(dto.getEmployeeId()).isPresent())
            throw new AuthException("Employee ID already exists: " + dto.getEmployeeId());

        Set<Role> roles = parseRoles(dto.getRoles());

        AuthEntity user = new AuthEntity();
        user.setEmployeeId(dto.getEmployeeId());
        user.setName(dto.getName());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(roles);
        user.setStatus(AccountStatus.ACTIVE);
        user.setFirstTimeLogin(true);
        user.setDepartment(dto.getDepartment());
        user.setCreatedAt(LocalDateTime.now());

        Set<UnitAssignment> assignments = new HashSet<>();
        for (UnitAssignmentDto u : dto.getAllowedLocations()) {
            if (u.getUnitCode() == null || u.getUnitCode().isBlank())
                throw new AuthException("Unit code is required for every assigned unit");
            assignments.add(buildAssignment(u));
        }
        user.setPrimaryLocation(dto.getAllowedLocations().get(0).getUnitCode());
        user.setAllowedLocations(assignments);

        AuthEntity saved = authRepository.save(user);

        saveRoleUnits(saved, dto.getRoleUnits());

        return toResponse(saved);
    }


    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return authRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        return toResponse(authRepository.findById(id)
                .orElseThrow(() -> new AuthException("User not found with id: " + id)));
    }


    @Override
    public UserResponseDto updateUser(Long id, UpdateUserDto dto) {

        AuthEntity user = authRepository.findById(id)
                .orElseThrow(() -> new AuthException("User not found with id: " + id));

        if (dto.getName() != null && !dto.getName().isBlank())
            user.setName(dto.getName());

        Set<Role> effectiveRoles = user.getRoles();

        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            effectiveRoles = parseRoles(dto.getRoles());
            user.setRoles(effectiveRoles);
        }

        if (dto.getPrimaryRole() != null && !dto.getPrimaryRole().isBlank()) {
            Role p;
            try {
                p = Role.valueOf(dto.getPrimaryRole().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new AuthException("Invalid primary role: " + dto.getPrimaryRole());
            }
            if (!user.hasRole(p))
                throw new AuthException("Primary role must be among assigned roles");
        }

        if (dto.getStatus() != null)
            user.setStatus(AccountStatus.valueOf(dto.getStatus().toUpperCase()));

        if (dto.getDepartment() != null && !dto.getDepartment().isBlank())
            user.setDepartment(dto.getDepartment());

        if (dto.getPrimaryLocation() != null)
            user.setPrimaryLocation(dto.getPrimaryLocation());

        if (dto.getAllowedLocations() != null) {
            user.getAllowedLocations().clear();
            for (UnitAssignmentDto u : dto.getAllowedLocations()) {
                user.getAllowedLocations().add(buildAssignment(u));
            }
        }

        AuthEntity saved = authRepository.save(user);

        if (dto.getRoleUnits() != null) {
            saveRoleUnits(saved, dto.getRoleUnits());
        }

        return toResponse(saved);
    }

    @Override
    public UserResponseDto updateLocations(Long id, UpdateLocationsDto dto) {
        AuthEntity user = authRepository.findById(id)
                .orElseThrow(() -> new AuthException("User not found with id: " + id));

        if (dto.getPrimaryLocation() != null)
            user.setPrimaryLocation(dto.getPrimaryLocation());

        if (dto.getAllowedLocations() != null) {
            user.getAllowedLocations().clear();
            for (UnitAssignmentDto u : dto.getAllowedLocations()) {
                user.getAllowedLocations().add(buildAssignment(u));
            }
        }

        return toResponse(authRepository.save(user));
    }

    @Override
    public UserResponseDto updateStatus(Long id, UpdateStatusDto dto) {
        if (dto.getStatus() == null)
            throw new AuthException("Status is required");

        AuthEntity user = authRepository.findById(id)
                .orElseThrow(() -> new AuthException("User not found with id: " + id));

        user.setStatus(dto.getStatus());
        return toResponse(authRepository.save(user));
    }


    @Override
    public void deleteUser(Long id) {
        if (!authRepository.existsById(id))
            throw new AuthException("User not found with id: " + id);

        userRoleUnitRepository.deleteByUserId(id);
        authRepository.deleteById(id);
    }

    @Override
    public String resetPassword(Long id, AuthEntity performedBy) {
        AuthEntity user = authRepository.findById(id)
                .orElseThrow(() -> new AuthException("User not found with id: " + id));

        String temp = "Tmp@" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        user.setPassword(passwordEncoder.encode(temp));
        user.setFirstTimeLogin(true);
        user.setPasswordLastReset(LocalDateTime.now());
        user.setResetBy(performedBy);
        authRepository.save(user);
        return temp;
    }


    @Override
    @Transactional(readOnly = true)
    public UserStatsDto getStats() {
        long total    = authRepository.count();
        long active   = authRepository.countByStatus(AccountStatus.ACTIVE);
        long inactive = total - active;
        return new UserStatsDto(total, active, inactive, 0, 0, 0, 0, 0);
    }

    /* ==================================================================
     *  PER-ROLE UNITS (repository-backed)
     * ================================================================ */

    /**
     * Overwrites the per-role unit rows for this user with the given map.
     * Delete-then-insert; runs in the same transaction as the caller.
     */
    private void saveRoleUnits(AuthEntity user, Map<String, List<String>> map) {

        userRoleUnitRepository.deleteByUserId(user.getId());

        if (map == null || map.isEmpty()) return;

        List<UserRoleUnit> rows = new ArrayList<>();
        map.forEach((role, codes) -> {
            if (role == null || codes == null) return;
            for (String code : codes) {
                if (code == null || code.isBlank()) continue;
                rows.add(new UserRoleUnit(user, role, code));
            }
        });

        if (!rows.isEmpty()) {
            userRoleUnitRepository.saveAll(rows);
        }
    }

    private Map<String, List<String>> loadRoleUnits(Long userId) {
        Map<String, List<String>> map = new HashMap<>();
        for (UserRoleUnit r : userRoleUnitRepository.findByUserId(userId)) {
            map.computeIfAbsent(r.getRole(), k -> new ArrayList<>())
                    .add(r.getUnitCode());
        }
        return map;
    }


    private Set<Role> parseRoles(List<String> roleNames) {
        try {
            return roleNames.stream()
                    .map(r -> Role.valueOf(r.trim().toUpperCase()))
                    .collect(Collectors.toCollection(() -> EnumSet.noneOf(Role.class)));
        } catch (IllegalArgumentException e) {
            throw new AuthException("Invalid role value");
        }
    }

    private Role parsePrimary(String primary, Set<Role> roles) {
        if (roles == null || roles.isEmpty())
            throw new AuthException("At least one role is required");

        Role p;
        if (primary != null && !primary.isBlank()) {
            try { p = Role.valueOf(primary.trim().toUpperCase()); }
            catch (IllegalArgumentException e) {
                throw new AuthException("Invalid primary role: " + primary);
            }
        } else {
            p = pickHighestPrivilegeRole(roles);
        }

        if (!roles.contains(p))
            throw new AuthException("Primary role must be among assigned roles");
        return p;
    }

    private Role pickHighestPrivilegeRole(Set<Role> roles) {
        for (Role r : ROLE_HIERARCHY) {
            if (roles.contains(r)) return r;
        }
        return roles.stream()
                .min((a, b) -> a.name().compareTo(b.name()))
                .orElseThrow(() -> new AuthException("No role assigned"));
    }

    private UnitAssignment buildAssignment(UnitAssignmentDto u) {
        UnitEntity master = unitRepository.findByUnitCode(u.getUnitCode()).orElse(null);
        UnitAssignment a = new UnitAssignment();
        a.setUnitCode(u.getUnitCode());
        a.setUnitName(nonBlank(u.getUnitName(), master != null ? master.getUnitName() : u.getUnitCode()));
        a.setAddress(nonBlank(u.getAddress(), master != null ? master.getAddress() : null));
        a.setPortCode(nonBlank(u.getPortCode(), master != null ? master.getPortCode() : null));
        a.setLatitude(u.getLatitude() != null ? u.getLatitude() : (master != null ? master.getLatitude() : null));
        a.setLongitude(u.getLongitude() != null ? u.getLongitude() : (master != null ? master.getLongitude() : null));
        a.setRadiusMeters(u.getRadiusMeters() != null ? u.getRadiusMeters()
                : (master != null && master.getRadiusMeters() != null ? master.getRadiusMeters() : 100));
        return a;
    }

    private String nonBlank(String a, String b) {
        return (a != null && !a.isBlank()) ? a : b;
    }


    private UserResponseDto toResponse(AuthEntity e) {
        UserResponseDto r = new UserResponseDto();
        r.setId(e.getId());
        r.setEmployeeId(e.getEmployeeId());
        r.setName(e.getName());

        List<String> roleNames = e.getRoles() == null
                ? List.of()
                : e.getRoles().stream().map(Enum::name).toList();
        r.setRoles(roleNames);

        r.setStatus(e.getStatus());
        r.setFirstTimeLogin(e.getFirstTimeLogin());
        r.setDepartment(e.getDepartment());
        r.setPrimaryLocation(e.getPrimaryLocation());
        r.setLastLoginAt(e.getLastLoginAt());
        r.setLastLoginLocation(e.getLastLoginLocation());
        r.setCreatedAt(e.getCreatedAt());
        r.setPasswordLastReset(e.getPasswordLastReset());
        r.setResetByName(e.getResetBy() != null ? e.getResetBy().getName() : null);

        if (e.getAllowedLocations() != null) {
            r.setAllowedLocations(
                    e.getAllowedLocations().stream()
                            .map(u -> new UnitAssignmentDto(
                                    u.getUnitCode(),
                                    u.getUnitName(),
                                    u.getAddress(),
                                    u.getPortCode(),
                                    u.getLatitude(),
                                    u.getLongitude(),
                                    u.getRadiusMeters()))
                            .toList());
        }

        r.setRoleUnits(loadRoleUnits(e.getId()));

        return r;
    }
}
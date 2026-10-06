package Service_Desk.BalPharma.auth.service;
import Service_Desk.BalPharma.auth.dto.AuthDto;
import Service_Desk.BalPharma.auth.dto.ChangePasswordDto;
import Service_Desk.BalPharma.auth.dto.LoginResponseDto;
import Service_Desk.BalPharma.auth.entity.AuthEntity;
import Service_Desk.BalPharma.auth.entity.UnitAssignment;
import Service_Desk.BalPharma.auth.repository.AuthRepository;
import Service_Desk.BalPharma.auth.repository.UserRoleUnitRepository;
import Service_Desk.BalPharma.exception.AuthException;
import Service_Desk.BalPharma.location.AccountStatus;
import Service_Desk.BalPharma.location.Location;
import Service_Desk.BalPharma.location.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
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
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthRepository authRepository;
    private final UserRoleUnitRepository userRoleUnitRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private static final Role[] ROLE_HIERARCHY = {
            Role.SUPER_MANAGER, Role.ADMIN, Role.DEPUTY_MANAGER,
            Role.EXECUTIVE, Role.EMPLOYEE,
    };


    @Override
    public LoginResponseDto login(AuthDto authDto, String clientIp) {

        AuthEntity user = authRepository.findByEmployeeId(authDto.getEmployeeId())
                .orElseThrow(() -> new AuthException("Employee ID not found"));

        if (!passwordEncoder.matches(authDto.getPassword(), user.getPassword()))
            throw new AuthException("Invalid password");

        if (user.getStatus() != AccountStatus.ACTIVE)
            throw new AuthException("Account is " + user.getStatus()
                    + ". Please contact administrator.");

        Role activeRole = resolveActiveRole(authDto.getRole(), user);

        boolean isPrivileged =
                activeRole == Role.SUPER_MANAGER || activeRole == Role.ADMIN;

        boolean hasCoords = authDto.getLatitude() != null
                && authDto.getLongitude() != null;

        if (!isPrivileged) {
            if (!hasCoords)
                throw new AuthException(
                        "Location coordinates are required for login.");

            Set<String> activeRoleCodes = resolveAllowedCodesForRole(user, activeRole);

            boolean inside = user.getAllowedLocations() != null
                    && user.getAllowedLocations().stream()
                    .filter(u -> activeRoleCodes.contains(u.getUnitCode()))
                    .anyMatch(u -> isInsideUnit(
                            u, authDto.getLatitude(), authDto.getLongitude()));

            if (!inside)
                throw new AuthException(
                        "Login denied. You are not within any unit assigned to the "
                                + activeRole.name() + " role (within 100 m).");
        }

        LocalDateTime now = LocalDateTime.now();
        String loc = resolveLocationFromIp(clientIp) != null
                ? resolveLocationFromIp(clientIp).name() : null;

        authRepository.updateLastLogin(user.getId(), now, loc);

        user.setLastLoginAt(now);
        user.setLastLoginLocation(loc);

        String token = jwtService.issue(user, activeRole);

        LoginResponseDto response = buildProfileResponse(user, activeRole);
        response.setToken(token);
        response.setMessage(
                Boolean.TRUE.equals(user.getFirstTimeLogin())
                        ? "First-time login. Please change your password."
                        : "Login successful.");
        return response;
    }


    @Override
    public void changePassword(ChangePasswordDto dto) {
        if (!dto.getNewPassword().equals(dto.getConfirmPassword()))
            throw new AuthException("New password and confirm password do not match");

        AuthEntity user = authRepository.findByEmployeeId(dto.getEmployeeId())
                .orElseThrow(() -> new AuthException("Employee ID not found"));

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword()))
            throw new AuthException("Old password is incorrect");

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setFirstTimeLogin(false);
        authRepository.save(user);
    }


    @Override
    @Transactional(readOnly = true)
    public LoginResponseDto getProfile(Long userId, Role activeRole) {
        AuthEntity user = authRepository.findById(userId)
                .orElseThrow(() -> new AuthException("User not found"));
        return buildProfileResponse(user, activeRole);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getLoginRoles(String employeeId) {
        AuthEntity user = authRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new AuthException("Employee ID not found"));
        return user.getRoles() == null
                ? List.of()
                : user.getRoles().stream().map(Enum::name).toList();
    }


    private LoginResponseDto buildProfileResponse(AuthEntity user, Role activeRole) {

        LoginResponseDto r = new LoginResponseDto();

        r.setUserId(user.getId());
        r.setEmployeeId(user.getEmployeeId());
        r.setName(user.getName());
        r.setInitials(initialsOf(user.getName()));
        r.setDepartment(user.getDepartment());

        r.setActiveRole(activeRole.name());
        r.setRoleDisplay(prettyRole(activeRole));
        r.setRoles(user.getRoles() == null
                ? List.of()
                : user.getRoles().stream().map(Enum::name).toList());

        r.setStatus(user.getStatus());
        r.setActive(user.getStatus() == AccountStatus.ACTIVE);
        r.setMustChangePassword(user.getFirstTimeLogin());

        Set<String> activeRoleCodes = resolveAllowedCodesForRole(user, activeRole);
        r.setAllowedLocations(activeRoleCodes);

        r.setPrimaryLocation(
                user.getPrimaryLocation() != null
                        && activeRoleCodes.contains(user.getPrimaryLocation())
                        ? user.getPrimaryLocation()
                        : activeRoleCodes.stream().findFirst().orElse(null));

        List<LoginResponseDto.UnitInfo> assigned = user.getAllowedLocations() == null
                ? List.of()
                : user.getAllowedLocations().stream()
                .filter(u -> activeRoleCodes.contains(u.getUnitCode()))
                .map(u -> {
                    LoginResponseDto.UnitInfo info = new LoginResponseDto.UnitInfo();
                    info.setUnitCode(u.getUnitCode());
                    info.setUnitName(u.getUnitName());
                    info.setPortCode(u.getPortCode());
                    info.setAddress(u.getAddress());
                    info.setLatitude(u.getLatitude());
                    info.setLongitude(u.getLongitude());
                    info.setRadiusMeters(
                            u.getRadiusMeters() != null ? u.getRadiusMeters() : 100);
                    return info;
                })
                .toList();

        r.setAssignedUnits(assigned);
        r.setLocationCount(assigned.size());

        r.setCreatedAt(user.getCreatedAt());
        r.setAccountAgeDays(user.getCreatedAt() == null
                ? 0
                : ChronoUnit.DAYS.between(user.getCreatedAt(), LocalDateTime.now()));
        r.setLastLoginAt(user.getLastLoginAt());
        r.setLastLoginLocation(user.getLastLoginLocation());

        return r;
    }


    private Role resolveActiveRole(String requestedRole, AuthEntity user) {
        if (user.getRoles() == null || user.getRoles().isEmpty())
            throw new AuthException("No role assigned to this user");

        if (requestedRole != null && !requestedRole.isBlank()) {
            Role requested;
            try {
                requested = Role.valueOf(requestedRole.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new AuthException("Invalid role: " + requestedRole);
            }
            if (!user.hasRole(requested))
                throw new AuthException(
                        "You are not allowed to log in as " + requested.name());
            return requested;
        }

        return pickHighestPrivilegeRole(user.getRoles());
    }

    private Role pickHighestPrivilegeRole(Set<Role> roles) {
        if (roles == null || roles.isEmpty())
            throw new AuthException("No role assigned to this user");
        for (Role r : ROLE_HIERARCHY) {
            if (roles.contains(r)) return r;
        }
        return roles.iterator().next();
    }

    private Set<String> resolveAllowedCodesForRole(AuthEntity user, Role activeRole) {

        if (activeRole == Role.ADMIN || activeRole == Role.SUPER_MANAGER) {
            Set<String> all = new HashSet<>();
            if (user.getAllowedLocations() != null)
                for (UnitAssignment u : user.getAllowedLocations())
                    all.add(u.getUnitCode());
            return all;
        }

        Set<String> roleScoped = userRoleUnitRepository
                .findByUserIdAndRole(user.getId(), activeRole.name())
                .stream()
                .map(u -> u.getUnitCode())
                .collect(Collectors.toSet());

        if (!roleScoped.isEmpty()) return roleScoped;

        // fallback — all assigned units
        Set<String> all = new HashSet<>();
        if (user.getPrimaryLocation() != null)
            all.add(user.getPrimaryLocation());
        if (user.getAllowedLocations() != null)
            for (UnitAssignment u : user.getAllowedLocations())
                all.add(u.getUnitCode());
        return all;
    }


    private boolean isInsideUnit(UnitAssignment u, double lat, double lng) {
        if (u.getLatitude() == null || u.getLongitude() == null) return false;
        double dist = haversineMeters(lat, lng, u.getLatitude(), u.getLongitude());
        int radius = u.getRadiusMeters() != null ? u.getRadiusMeters() : 100;
        return dist <= radius;
    }

    private double haversineMeters(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private Location resolveLocationFromIp(String ip) {
        if (ip == null) return null;
        if (ip.equals("0:0:0:0:0:0:0:1") || ip.equals("127.0.0.1"))
            return null;
        return null;
    }

    private String prettyRole(Role r) {
        if (r == null) return null;
        String[] parts = r.name().split("_");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p.isEmpty()) continue;
            sb.append(Character.toUpperCase(p.charAt(0)))
                    .append(p.substring(1).toLowerCase())
                    .append(' ');
        }
        return sb.toString().trim();
    }

    private String initialsOf(String name) {
        if (name == null || name.isBlank()) return "";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1)
            return parts[0].substring(0, 1).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }
}
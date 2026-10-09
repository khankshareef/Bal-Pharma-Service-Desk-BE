package Service_Desk.BalPharma.config;

import Service_Desk.BalPharma.auth.entity.AuthEntity;
import Service_Desk.BalPharma.auth.entity.UnitAssignment;
import Service_Desk.BalPharma.auth.entity.UserRoleUnit;
import Service_Desk.BalPharma.auth.repository.AuthRepository;
import Service_Desk.BalPharma.auth.repository.UserRoleUnitRepository;
import Service_Desk.BalPharma.location.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LocationEnforcementFilter extends OncePerRequestFilter {

    private final AuthRepository authRepository;
    private final UserRoleUnitRepository userRoleUnitRepository;

    private static final List<String> BYPASS = List.of(
            "/auth/login",
            "/auth/roles-for-user",
            "/auth/change-password",
            "/auth/me",
            "/files/**",
            "/socket.io/**",
            "/ws/**",
            "/error"
    );

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    private boolean isBypassed(String path) {
        return BYPASS.stream().anyMatch(p -> MATCHER.match(p, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            chain.doFilter(req, res);
            return;
        }

        String path = req.getRequestURI();
        if (isBypassed(path)) {
            chain.doFilter(req, res);
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            chain.doFilter(req, res);
            return;
        }

        String activeRoleStr = jwt.getClaim("activeRole");
        if (activeRoleStr == null) {
            chain.doFilter(req, res);
            return;
        }

        Role activeRole;
        try {
            activeRole = Role.valueOf(activeRoleStr);
        } catch (IllegalArgumentException e) {
            chain.doFilter(req, res);
            return;
        }

        if (activeRole == Role.SUPER_MANAGER || activeRole == Role.ADMIN) {
            chain.doFilter(req, res);
            return;
        }

        Long userId = jwt.getClaim("userId");
        String latStr = req.getHeader("X-Client-Latitude");
        String lngStr = req.getHeader("X-Client-Longitude");

        if (latStr == null || lngStr == null) {
            res.setStatus(428);
            res.setContentType("application/json");
            res.getWriter().write(
                    "{\"error\":\"Location coordinates required.\"}");
            return;
        }

        double lat, lng;
        try {
            lat = Double.parseDouble(latStr);
            lng = Double.parseDouble(lngStr);
        } catch (NumberFormatException e) {
            res.setStatus(400);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\":\"Invalid coordinates.\"}");
            return;
        }

        AuthEntity user = authRepository.findById(userId).orElse(null);
        if (user == null) {
            res.setStatus(401);
            return;
        }

        Set<String> activeRoleCodes = userRoleUnitRepository
                .findByUserIdAndRole(user.getId(), activeRole.name())
                .stream()
                .map(UserRoleUnit::getUnitCode)
                .collect(Collectors.toSet());

        if (activeRoleCodes.isEmpty() && user.getAllowedLocations() != null) {
            activeRoleCodes = user.getAllowedLocations().stream()
                    .map(UnitAssignment::getUnitCode)
                    .collect(Collectors.toSet());
        }

        final Set<String> codes = activeRoleCodes;

        boolean inside = user.getAllowedLocations() != null
                && user.getAllowedLocations().stream()
                .filter(u -> codes.contains(u.getUnitCode()))
                .anyMatch(u -> isInside(u, lat, lng));

        if (!inside) {
            res.setStatus(403);
            res.setContentType("application/json");
            res.getWriter().write(
                    "{\"error\":\"You are not inside any unit assigned to the "
                            + activeRole.name() + " role (within 100 m).\"}");
            return;
        }

        chain.doFilter(req, res);
    }

    private boolean isInside(UnitAssignment u, double lat, double lng) {
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
}
package Service_Desk.BalPharma.auth.controller;

import Service_Desk.BalPharma.auth.dto.AuthDto;
import Service_Desk.BalPharma.auth.dto.ChangePasswordDto;
import Service_Desk.BalPharma.auth.dto.LoginResponseDto;
import Service_Desk.BalPharma.auth.service.AuthService;
import Service_Desk.BalPharma.location.Role;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody AuthDto authDto,
                                                  HttpServletRequest request) {
        String clientIp = extractClientIp(request);
        return ResponseEntity.ok(authService.login(authDto, clientIp));
    }


    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDto dto) {
        authService.changePassword(dto);
        return ResponseEntity.ok("Password changed successfully. Please login again.");
    }

    @GetMapping("/me")
    public ResponseEntity<LoginResponseDto> me(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        String activeRoleStr = jwt.getClaim("activeRole");

        if (userId == null || activeRoleStr == null) {
            return ResponseEntity.status(401).build();
        }

        Role activeRole = Role.valueOf(activeRoleStr);
        return ResponseEntity.ok(authService.getProfile(userId, activeRole));
    }

    @PostMapping("/roles-for-user")
    public ResponseEntity<List<String>> rolesForUser(@RequestBody Map<String, String> body) {
        String employeeId = body.get("employeeId");
        if (employeeId == null || employeeId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(authService.getLoginRoles(employeeId));
    }

    private String extractClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isEmpty()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
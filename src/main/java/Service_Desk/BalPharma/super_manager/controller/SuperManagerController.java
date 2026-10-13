package Service_Desk.BalPharma.super_manager.controller;

import Service_Desk.BalPharma.auth.entity.AuthEntity;
import Service_Desk.BalPharma.auth.repository.AuthRepository;
import Service_Desk.BalPharma.exception.AuthException;
import Service_Desk.BalPharma.super_manager.dto.*;
import Service_Desk.BalPharma.super_manager.service.SuperManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/super-manager/users")
@RequiredArgsConstructor
public class SuperManagerController {

    private final SuperManagerService superManagerService;
    private final AuthRepository authRepository;

    @PostMapping
    public ResponseEntity<UserResponseDto> create(@RequestBody CreateUserDto dto) {
        return ResponseEntity.ok(superManagerService.createUser(dto));
    }

    @GetMapping("/stats")
    public ResponseEntity<UserStatsDto> stats() {
        return ResponseEntity.ok(superManagerService.getStats());
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAll() {
        return ResponseEntity.ok(superManagerService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(superManagerService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> update(
            @PathVariable Long id,
            @RequestBody UpdateUserDto dto) {
        return ResponseEntity.ok(superManagerService.updateUser(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        superManagerService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PutMapping("/{id}/locations")
    public ResponseEntity<UserResponseDto> updateLocations(
            @PathVariable Long id,
            @RequestBody UpdateLocationsDto dto) {
        return ResponseEntity.ok(superManagerService.updateLocations(id, dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponseDto> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusDto dto) {
        return ResponseEntity.ok(superManagerService.updateStatus(id, dto));
    }

    @PatchMapping("/{id}/reset-password")
    public ResponseEntity<?> resetPassword(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        String employeeId = jwt.getSubject();
        AuthEntity performedBy = authRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new AuthException("Actor not found"));

        String tempPassword = superManagerService.resetPassword(id, performedBy);

        return ResponseEntity.ok(Map.of(
                "tempPassword", tempPassword,
                "message", "Password reset. User must change it on next login."
        ));
    }
}
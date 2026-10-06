package Service_Desk.BalPharma.notification.controller;

import Service_Desk.BalPharma.auth.entity.AuthEntity;
import Service_Desk.BalPharma.auth.repository.AuthRepository;
import Service_Desk.BalPharma.exception.AuthException;
import Service_Desk.BalPharma.notification.dto.NotificationResponseDto;
import Service_Desk.BalPharma.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final AuthRepository authRepository;

    @GetMapping("/user/{employeeId}")
    public ResponseEntity<List<NotificationResponseDto>> getForUser(
            @PathVariable String employeeId) {
        return ResponseEntity.ok(
                notificationService.getForUser(resolveUserId(employeeId)));
    }

    @GetMapping("/user/{employeeId}/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @PathVariable String employeeId) {
        return ResponseEntity.ok(Map.of(
                "count",
                notificationService.getUnreadCount(resolveUserId(employeeId))));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable Long id,
            @RequestParam String employeeId) {
        notificationService.markAsRead(id, resolveUserId(employeeId));
        return ResponseEntity.ok("Marked as read");
    }

    @PatchMapping("/user/{employeeId}/read-all")
    public ResponseEntity<?> markAllAsRead(@PathVariable String employeeId) {
        notificationService.markAllAsRead(resolveUserId(employeeId));
        return ResponseEntity.ok("All marked as read");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return ResponseEntity.ok("Deleted");
    }

    private Long resolveUserId(String employeeId) {
        AuthEntity user = authRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new AuthException(
                        "Employee not found: " + employeeId));
        return user.getId();
    }
}
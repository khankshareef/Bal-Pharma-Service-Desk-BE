package Service_Desk.BalPharma.super_manager.controller;
import Service_Desk.BalPharma.super_manager.dto.*;
import Service_Desk.BalPharma.super_manager.service.SuperManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/super-manager/users")
@RequiredArgsConstructor
public class SuperManagerController {

    private final SuperManagerService superManagerService;
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
    public ResponseEntity<UserResponseDto> update(@PathVariable Long id,
                                                  @RequestBody UpdateUserDto dto) {
        return ResponseEntity.ok(superManagerService.updateUser(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        superManagerService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PutMapping("/{id}/locations")
    public ResponseEntity<UserResponseDto> updateLocations(@PathVariable Long id,
                                                           @RequestBody UpdateLocationsDto dto) {
        return ResponseEntity.ok(superManagerService.updateLocations(id, dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponseDto> updateStatus(@PathVariable Long id,
                                                        @RequestBody UpdateStatusDto dto) {
        return ResponseEntity.ok(superManagerService.updateStatus(id, dto));
    }
}
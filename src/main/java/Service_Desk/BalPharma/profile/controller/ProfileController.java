package Service_Desk.BalPharma.profile.controller;

import Service_Desk.BalPharma.profile.dto.ProfileResponseDto;
import Service_Desk.BalPharma.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{employeeId}")
    public ResponseEntity<ProfileResponseDto> getProfile(
            @PathVariable String employeeId
    ) {
        return ResponseEntity.ok(profileService.getByEmployeeId(employeeId));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ProfileResponseDto> getProfileById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(profileService.getById(id));
    }
}
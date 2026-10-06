package Service_Desk.BalPharma.super_manager.dto;

import Service_Desk.BalPharma.location.AccountStatus;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class UserResponseDto {
    private Long id;
    private String employeeId;
    private String name;

    private List<String> roles;
    private String primaryRole;
    private String role;

    private AccountStatus status;
    private Boolean firstTimeLogin;
    private String department;
    private String primaryLocation;
    private List<UnitAssignmentDto> allowedLocations;
    private LocalDateTime lastLoginAt;
    private String lastLoginLocation;
    private Map<String, List<String>> roleUnits;
    private LocalDateTime createdAt;
    private LocalDateTime passwordLastReset;
    private String resetByName;
}
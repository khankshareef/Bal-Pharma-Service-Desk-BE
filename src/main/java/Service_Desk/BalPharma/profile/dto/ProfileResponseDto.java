package Service_Desk.BalPharma.profile.dto;

import Service_Desk.BalPharma.location.AccountStatus;
import Service_Desk.BalPharma.super_manager.dto.UnitAssignmentDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProfileResponseDto {

    private Long id;
    private String employeeId;
    private String name;
    private String initials;

    private String activeRole;

    private String roleDisplay;

    private List<String> roles;

    private String role;

    private String department;

    private AccountStatus status;
    private Boolean firstTimeLogin;
    private Boolean active;

    private String primaryLocation;
    private List<UnitAssignmentDto> assignedUnits;
    private Integer locationCount;

    private LocalDateTime lastLoginAt;
    private String lastLoginLocation;

    private LocalDateTime createdAt;
    private LocalDateTime passwordLastReset;
    private String resetByName;
    private Long accountAgeDays;
}
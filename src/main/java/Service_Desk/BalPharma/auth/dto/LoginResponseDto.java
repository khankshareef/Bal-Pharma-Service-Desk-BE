package Service_Desk.BalPharma.auth.dto;

import Service_Desk.BalPharma.location.AccountStatus;
import Service_Desk.BalPharma.location.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {

    private Long userId;
    private String employeeId;
    private String name;
    private String initials;

    private String role;

    private String primaryRole;
    private String activeRole;
    private String roleDisplay;
    private List<String> roles;

    private AccountStatus status;
    private Boolean active;
    private Boolean mustChangePassword;
    private String department;

    private Location currentLocation;
    private String primaryLocation;
    private Set<String> allowedLocations;
    private List<UnitInfo> assignedUnits;

    private LocalDateTime createdAt;
    private long accountAgeDays;
    private LocalDateTime lastLoginAt;
    private String lastLoginLocation;
    private int locationCount;

    private String message;
    private String token;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UnitInfo {
        private String unitCode;
        private String unitName;
        private String portCode;

        private String address;
        private Double latitude;
        private Double longitude;
        private Integer radiusMeters;
    }
}
package Service_Desk.BalPharma.super_manager.dto;

import lombok.Data;
import java.util.Map;
import java.util.List;

@Data
public class UpdateUserDto {
    private String name;
    private List<String> roles;
    private String primaryRole;
    private String status;
    private String department;
    private String primaryLocation;
    private Map<String, List<String>> roleUnits;
    private List<UnitAssignmentDto> allowedLocations;
}
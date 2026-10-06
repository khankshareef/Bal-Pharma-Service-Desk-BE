package Service_Desk.BalPharma.super_manager.dto;

import lombok.Data;
import java.util.Map;
import java.util.List;

@Data
public class CreateUserDto {

    private String employeeId;
    private String name;
    private String password;
    private List<String> roles;
    private String primaryRole;
    private String department;
    private Map<String, List<String>> roleUnits;
    private List<UnitAssignmentDto> allowedLocations;
}
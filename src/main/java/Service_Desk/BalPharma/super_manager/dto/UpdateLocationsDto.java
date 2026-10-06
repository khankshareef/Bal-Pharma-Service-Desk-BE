package Service_Desk.BalPharma.super_manager.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateLocationsDto {
    private String primaryLocation;
    private List<UnitAssignmentDto> allowedLocations;
}
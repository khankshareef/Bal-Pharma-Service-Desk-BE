package Service_Desk.BalPharma.super_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UnitAssignmentDto {
    private String unitCode;
    private String unitName;
    private String address;
    private String portCode;
    private Double latitude;
    private Double longitude;
    private Integer radiusMeters;
}
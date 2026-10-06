package Service_Desk.BalPharma.unit.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUnitDto {
    private String unitName;
    private String portCode;
    private Double latitude;
    private Double longitude;
    private Integer radiusMeters;
    private Boolean active;
}
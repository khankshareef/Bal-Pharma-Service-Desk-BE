package Service_Desk.BalPharma.unit.dto;

import Service_Desk.BalPharma.unit.entity.UnitEntity;
import lombok.Data;

@Data
public class UnitDto {
    private Long id;
    private String unitCode;
    private String unitName;
    private String address;
    private String portCode;
    private Double latitude;
    private Double longitude;
    private Integer radiusMeters;
    private Boolean active;

    public static UnitDto from(UnitEntity e) {
        UnitDto dto = new UnitDto();
        dto.setId(e.getId());
        dto.setUnitCode(e.getUnitCode());
        dto.setUnitName(e.getUnitName());
        dto.setAddress(e.getAddress());
        dto.setPortCode(e.getPortCode());
        dto.setLatitude(e.getLatitude());
        dto.setLongitude(e.getLongitude());
        dto.setRadiusMeters(e.getRadiusMeters());
        dto.setActive(e.getActive());
        return dto;
    }
}
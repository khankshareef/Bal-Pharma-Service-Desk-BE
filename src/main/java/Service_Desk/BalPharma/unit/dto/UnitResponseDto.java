package Service_Desk.BalPharma.unit.dto;

import Service_Desk.BalPharma.unit.entity.UnitEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UnitResponseDto {
    private Long id;
    private String unitCode;
    private String unitName;
    private String address;
    private String portCode;
    private Double latitude;
    private Double longitude;
    private Integer radiusMeters;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UnitResponseDto from(UnitEntity u) {
        return new UnitResponseDto(
                u.getId(),
                u.getUnitCode(),
                u.getUnitName(),
                u.getAddress(),
                u.getPortCode(),
                u.getLatitude(),
                u.getLongitude(),
                u.getRadiusMeters(),
                u.getActive(),
                u.getCreatedAt(),
                u.getUpdatedAt()
        );
    }
}
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
    private String  address;
    private String portCode;
    private Double latitude;
    private Double longitude;
    private Integer radiusMeters;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private long users;         // total users assigned to this unit
    private long executives;    // subset of those users with EXECUTIVE role

    public static UnitResponseDto from(UnitEntity u) {
        return from(u, 0L, 0L);
    }

    public static UnitResponseDto from(UnitEntity u, long users, long executives) {
        UnitResponseDto dto = new UnitResponseDto();
        dto.setId(u.getId());
        dto.setUnitCode(u.getUnitCode());
        dto.setUnitName(u.getUnitName());
        dto.setAddress(u.getAddress());
        dto.setPortCode(u.getPortCode());
        dto.setLatitude(u.getLatitude());
        dto.setLongitude(u.getLongitude());
        dto.setRadiusMeters(u.getRadiusMeters());
        dto.setActive(u.getActive());
        dto.setCreatedAt(u.getCreatedAt());
        dto.setUpdatedAt(u.getUpdatedAt());
        dto.setUsers(users);
        dto.setExecutives(executives);
        return dto;
    }
}
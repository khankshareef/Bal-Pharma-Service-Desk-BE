package Service_Desk.BalPharma.super_manager.dto;

import Service_Desk.BalPharma.location.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LocationDisplayDto {
    private Location unit;
    private String unitName;

    public static LocationDisplayDto from(Location loc) {
        if (loc == null) return null;
        return new LocationDisplayDto(
                loc,
                loc.getDisplayName() + ": " + loc.getCity()
        );
    }
}
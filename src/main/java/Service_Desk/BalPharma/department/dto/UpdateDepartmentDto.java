package Service_Desk.BalPharma.department.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDepartmentDto {
    private String name;
    private Long unitId;
    private Boolean active;
}
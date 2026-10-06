package Service_Desk.BalPharma.department.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDepartmentDto {
    private String name;
    private Long unitId;
    private Boolean active;
}
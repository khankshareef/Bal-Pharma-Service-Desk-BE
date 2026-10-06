package Service_Desk.BalPharma.department.dto;

import Service_Desk.BalPharma.department.entity.DepartmentEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class DepartmentResponseDto {
    private Long id;
    private String departmentCode;
    private String name;
    private Long unitId;
    private String unitName;
    private Boolean active;
    private long users;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DepartmentResponseDto from(DepartmentEntity d) {
        return new DepartmentResponseDto(
                d.getId(),
                d.getDepartmentCode(),
                d.getName(),
                d.getUnit() != null ? d.getUnit().getId() : null,
                d.getUnit() != null ? d.getUnit().getUnitName() : "All Units",
                d.getActive(),
                0L,
                d.getCreatedAt(),
                d.getUpdatedAt()
        );
    }
}
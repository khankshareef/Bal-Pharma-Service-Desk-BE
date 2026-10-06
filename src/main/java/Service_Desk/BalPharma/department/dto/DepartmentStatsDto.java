package Service_Desk.BalPharma.department.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentStatsDto {
    private long total;
    private long active;
    private long inactive;
    private long assignedUsers;
}
package Service_Desk.BalPharma.super_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserStatsDto {
    private long totalUsers;
    private long activeUsers;
    private long inactiveUsers;
    private long employees;
    private long executives;
    private long deputyManagers;
    private long superManagers;
    private long admins;
}
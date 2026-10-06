package Service_Desk.BalPharma.dashboard.dto;

import lombok.Data;

import java.util.List;

@Data
public class SuperManagerDashboardDto {
    private long totalUsers;
    private long activeUsers;
    private long employees;
    private long executives;
    private long locations;
    private String totalUsersHint;
    private String activeUsersHint;
    private String employeesHint;
    private String executivesHint;
    private String locationsHint;
    private List<UnitCount> units;
    private List<ActivityItem> recentActivity;

    @Data
    public static class UnitCount {
        private String unitCode;
        private String unitName;
        private String address;
        private long userCount;
    }

    @Data
    public static class ActivityItem {
        private Long id;
        private String time;
        private String activity;
    }
}
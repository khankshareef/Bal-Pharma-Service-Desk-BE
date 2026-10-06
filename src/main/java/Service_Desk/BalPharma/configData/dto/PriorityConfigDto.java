package Service_Desk.BalPharma.configData.dto;

import lombok.Data;

@Data
public class PriorityConfigDto {
    private Long id;
    private String priorityId;
    private String name;
    private long tickets;
    private Boolean active;
}
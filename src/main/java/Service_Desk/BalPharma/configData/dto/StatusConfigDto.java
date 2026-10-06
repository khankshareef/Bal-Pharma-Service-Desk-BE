package Service_Desk.BalPharma.configData.dto;

import lombok.Data;

@Data
public class StatusConfigDto {
    private Long id;
    private String statusId;
    private String name;
    private long tickets;
    private Boolean active;
}
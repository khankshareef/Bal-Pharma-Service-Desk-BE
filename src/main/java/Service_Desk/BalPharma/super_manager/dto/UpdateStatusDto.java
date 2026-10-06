package Service_Desk.BalPharma.super_manager.dto;

import Service_Desk.BalPharma.location.AccountStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStatusDto {
    private AccountStatus status;
}
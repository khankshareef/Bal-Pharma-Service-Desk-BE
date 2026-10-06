package Service_Desk.BalPharma.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDto {
    private String employeeId;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
package Service_Desk.BalPharma.auth.dto;

import lombok.Data;

import java.util.List;

@Data
public class AuthDto {
    private String employeeId;
    private String password;
    private String role;
    private List<String> roles;
    private String primaryRole;
    private Double latitude;
    private Double longitude;
}
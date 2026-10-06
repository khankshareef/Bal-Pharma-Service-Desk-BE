package Service_Desk.BalPharma.auth.service;

import Service_Desk.BalPharma.auth.dto.AuthDto;
import Service_Desk.BalPharma.auth.dto.ChangePasswordDto;
import Service_Desk.BalPharma.auth.dto.LoginResponseDto;
import Service_Desk.BalPharma.location.Role;

import java.util.List;

public interface AuthService {
    LoginResponseDto login(AuthDto authDto, String clientIp);
    void changePassword(ChangePasswordDto dto);
    LoginResponseDto getProfile(Long userId, Role activeRole);
    List<String> getLoginRoles(String employeeId);
}
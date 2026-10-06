package Service_Desk.BalPharma.profile.service;

import Service_Desk.BalPharma.location.Role;
import Service_Desk.BalPharma.profile.dto.ProfileResponseDto;

public interface ProfileService {

    ProfileResponseDto getByEmployeeId(String employeeId, Role activeRole);
    ProfileResponseDto getById(Long id, Role activeRole);

    ProfileResponseDto getByEmployeeId(String employeeId);
    ProfileResponseDto getById(Long id);
}
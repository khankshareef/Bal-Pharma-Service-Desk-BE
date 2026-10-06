package Service_Desk.BalPharma.super_manager.service;

import Service_Desk.BalPharma.auth.entity.AuthEntity;
import Service_Desk.BalPharma.super_manager.dto.*;

import java.util.List;

public interface SuperManagerService {
    UserResponseDto createUser(CreateUserDto dto);
    List<UserResponseDto> getAllUsers();
    UserResponseDto getUserById(Long id);
    UserResponseDto updateUser(Long id, UpdateUserDto dto);
    UserResponseDto updateLocations(Long id, UpdateLocationsDto dto);
    UserResponseDto updateStatus(Long id, UpdateStatusDto dto);
    void deleteUser(Long id);
    UserStatsDto getStats();
    String resetPassword(Long id, AuthEntity performedBy);
}
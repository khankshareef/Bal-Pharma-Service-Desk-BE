package Service_Desk.BalPharma.rating.service;

import Service_Desk.BalPharma.rating.dto.CreateRatingDto;
import Service_Desk.BalPharma.rating.dto.RatingResponseDto;
import Service_Desk.BalPharma.rating.dto.UpdateRatingDto;

import java.util.List;

public interface RatingService {
    RatingResponseDto create(CreateRatingDto dto, String employeeId);
    RatingResponseDto getById(Long id);
    List<RatingResponseDto> getAll();
    List<RatingResponseDto> getByTicket(Long ticketId);
    List<RatingResponseDto> getByEmployee(String employeeId);
    RatingResponseDto update(Long id, UpdateRatingDto dto, String employeeId);
    void delete(Long id);
    Double averageForTicket(Long ticketId);
}
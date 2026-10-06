package Service_Desk.BalPharma.unit.service;

import Service_Desk.BalPharma.unit.dto.CreateUnitDto;
import Service_Desk.BalPharma.unit.dto.UpdateUnitDto;
import Service_Desk.BalPharma.unit.dto.UnitResponseDto;

import java.util.List;

public interface UnitService {
    UnitResponseDto create(CreateUnitDto dto);
    List<UnitResponseDto> getAll();
    UnitResponseDto getById(Long id);
    UnitResponseDto update(Long id, UpdateUnitDto dto);
    void delete(Long id);
}
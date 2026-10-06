package Service_Desk.BalPharma.unit.service;

import Service_Desk.BalPharma.exception.AuthException;
import Service_Desk.BalPharma.unit.dto.*;
import Service_Desk.BalPharma.unit.entity.UnitEntity;
import Service_Desk.BalPharma.unit.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;

    @Override
    public UnitResponseDto create(CreateUnitDto dto) {

        if (dto.getUnitName() == null || dto.getUnitName().isBlank())
            throw new AuthException("Unit name is required");


        validateCoordinates(dto.getLatitude(), dto.getLongitude());

        String code = dto.getUnitCode();
        if (code == null || code.isBlank()) {
            code = dto.getUnitName().trim().toUpperCase()
                    .replaceAll("\\s+", "_")
                    + "_" + System.currentTimeMillis();
        }

        if (unitRepository.existsByUnitCode(code))
            throw new AuthException("Unit code already exists: " + code);

        UnitEntity unit = new UnitEntity();
        unit.setUnitCode(code);
        unit.setUnitName(dto.getUnitName().trim());
        unit.setAddress(dto.getAddress().trim());
        unit.setPortCode(dto.getPortCode() != null ? dto.getPortCode().trim() : null);

        unit.setLatitude(dto.getLatitude());
        unit.setLongitude(dto.getLongitude());
        unit.setRadiusMeters(dto.getRadiusMeters() != null ? dto.getRadiusMeters() : 500);

        unit.setActive(true);
        return UnitResponseDto.from(unitRepository.save(unit));
    }
    @Override
    public List<UnitResponseDto> getAll() {
        return unitRepository.findAll().stream()
                .map(UnitResponseDto::from)
                .toList();
    }

    @Override
    public UnitResponseDto getById(Long id) {
        return UnitResponseDto.from(unitRepository.findById(id)
                .orElseThrow(() -> new AuthException("Unit not found with id: " + id)));
    }

    @Override
    public UnitResponseDto update(Long id, UpdateUnitDto dto) {

        UnitEntity unit = unitRepository.findById(id)
                .orElseThrow(() -> new AuthException("Unit not found with id: " + id));

        if (dto.getUnitName() != null && !dto.getUnitName().isBlank())
            unit.setUnitName(dto.getUnitName().trim());

        if (dto.getPortCode() != null)
            unit.setPortCode(dto.getPortCode().trim());

        if (dto.getLatitude() != null || dto.getLongitude() != null) {
            Double lat = dto.getLatitude() != null ? dto.getLatitude() : unit.getLatitude();
            Double lng = dto.getLongitude() != null ? dto.getLongitude() : unit.getLongitude();
            validateCoordinates(lat, lng);
            unit.setLatitude(lat);
            unit.setLongitude(lng);
        }

        if (dto.getRadiusMeters() != null)
            unit.setRadiusMeters(dto.getRadiusMeters());

        if (dto.getActive() != null)
            unit.setActive(dto.getActive());

        return UnitResponseDto.from(unitRepository.save(unit));
    }

    @Override
    public void delete(Long id) {
        if (!unitRepository.existsById(id))
            throw new AuthException("Unit not found with id: " + id);
        unitRepository.deleteById(id);
    }

    private void validateCoordinates(Double lat, Double lng) {
        if (lat != null && (lat < -90 || lat > 90))
            throw new AuthException("Latitude must be between -90 and 90");
        if (lng != null && (lng < -180 || lng > 180))
            throw new AuthException("Longitude must be between -180 and 180");
    }
}
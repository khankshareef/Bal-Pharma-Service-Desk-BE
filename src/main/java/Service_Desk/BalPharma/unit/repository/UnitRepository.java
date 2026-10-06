package Service_Desk.BalPharma.unit.repository;

import Service_Desk.BalPharma.unit.entity.UnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UnitRepository extends JpaRepository<UnitEntity, Long> {

    List<UnitEntity> findAllByActiveTrueOrderByUnitNameAsc();

    Optional<UnitEntity> findByUnitCode(String unitCode);

    boolean existsByUnitCode(String unitCode);
}
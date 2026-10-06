package Service_Desk.BalPharma.configData.repository;

import Service_Desk.BalPharma.configData.entity.PriorityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriorityRepository extends JpaRepository<PriorityEntity, Long> {
    List<PriorityEntity> findAllByOrderByDisplayOrderAsc();
}